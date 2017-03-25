package com.wrpinheiro.deadcodedetection.service.analysis;

import com.wrpinheiro.deadcodedetection.exceptions.AnalysisException;
import com.wrpinheiro.deadcodedetection.model.AnalysisStatus;
import com.wrpinheiro.deadcodedetection.model.DeadCodeIssue;
import com.wrpinheiro.deadcodedetection.model.Repository;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static java.util.Collections.singletonList;
import static java.util.Comparator.reverseOrder;

/**
 * Created by wrpinheiro on 3/24/17.
 */
@Slf4j
@Service
public class AnalysisServiceImpl implements AnalysisService {

    private static final String REPOSITORIES_SUBDIR = "repos/";

    @Value("${app.analyzer.dataDir}")
    private String dataDir;

    @Value("${app.analyzer.scriptsDir}")
    private String scriptsDir;

    @Value("${app.analyzer.scitoolsHome}")
    private String scitoolsHome;

    private static final String BRANCH_TO_ANALYZE = "master";

    @Override
    @Async
    public void analyse(Repository repository) {
        log.info("Starting analysis for repository {}", repository.getUrl());

        repository.setStatus(AnalysisStatus.PROCESSING);

        try {
            Path repositoryDir = cloneGitHubRepository(repository);
            Path udbFile = createUDBFile(repository, repositoryDir, dataDir);
            String deadCodeIssuesOutput = checkDeadCodeAnalysis(repository, udbFile);

            parseDeadCodeIssues(repository, deadCodeIssuesOutput);

            repository.setStatus(AnalysisStatus.COMPLETED);
            repository.setProcessedAt(new Date());
        } catch (AnalysisException ex) {
            repository.setErrorMessage(ex.getMessage());
            repository.setStatus(AnalysisStatus.FAILED);
        }
    }

    private DeadCodeIssue parseDeadCodeIssue(String kind, String[] location) {
        return DeadCodeIssue.builder()
                .kind(kind)
                .filename(location[2].trim())
                .fromLine(Integer.valueOf(location[4].trim()))
                .toLine(Integer.valueOf(location[5].trim()))
                .ref(location[0].trim())
                .build();
    }

    /**
     * The expected deadCodeOutput is something like:
     *
     * 0 = "@Class"
     * 1 = "\tMyBufferedReader;[File:;/Users/wrpinheiro/.deadCodeDetection/repos/wrpinheiro/diversos/jdk7post2/MyBufferedReader.java;Line:;5;14;]"
     * 2 = "@Parameter"
     * 3 = "\tGerenciadorBomba.main.args;[File:;/Users/wrpinheiro/.deadCodeDetection/repos/wrpinheiro/diversos/infoq/jdk7/GerenciadorBomba.java;Line:;13;13;]"
     * 4 = "\tGerenciadorRecursosMultiCatch.main.args;[File:;/Users/wrpinheiro/.deadCodeDetection/repos/wrpinheiro/diversos/infoq/jdk7/GerenciadorRecursosMultiCatch.java;Line:;19;19;]"
     * 5 = "\tInferenciaGenerics.main.args;[File:;/Users/wrpinheiro/.deadCodeDetection/repos/wrpinheiro/diversos/infoq/jdk7/InferenciaGenerics.java;Line:;8;8;]"
     * 6 = "\tSeparadorLiteraisNumericos.main.args;[File:;/Users/wrpinheiro/.deadCodeDetection/repos/wrpinheiro/diversos/infoq/jdk7/SeparadorLiteraisNumericos.java;Line:;2;2;]"
     *
     * The kind of dead code starts with an "@" and the information about the dead code (the file, lines, etc) and in a dot comma separated string
     *
     * @param repository
     * @param deadCodeOutput
     */
    private void parseDeadCodeIssues(Repository repository, String deadCodeOutput) {
        log.info("Creating instances of dead code issues for repository {}", repository.getUrl());

        List<DeadCodeIssue> deadCodeIssues = new ArrayList<>();

        String lastType = "";
        for (String s: deadCodeOutput.split("\\?")) {
            if (s.startsWith("@")) {
                lastType = s.substring(1);
            } else if (!s.trim().equals("")){
                String[] location = s.split(";");

                deadCodeIssues.add(parseDeadCodeIssue(lastType, location));
            }
        }

        deadCodeIssues.sort(Comparator.comparing(DeadCodeIssue::getFilename));

        repository.setDeadCodeIssues(deadCodeIssues);
    }

    private String checkDeadCodeAnalysis(Repository repository, Path udbFile) {
        log.info("Checking dead code for repository {}", repository.getUrl());

        final String UPERL_FILE = scitoolsHome + "/uperl";
        final String UNUSED_CODE_SCRIPT = scriptsDir + "/acjf_unused_modified.pl";

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(UPERL_FILE, UNUSED_CODE_SCRIPT, "-db",
                    udbFile.toAbsolutePath().toString(), "-byKind");

            Process p = processBuilder.start();

            BufferedReader stdout = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader stderr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            String line;

            StringBuilder errorLog = new StringBuilder();
            StringBuilder outputLog = new StringBuilder();

            while ((line = stdout.readLine()) != null) {
                outputLog.append(line);
            }

            while ((line = stderr.readLine()) != null) {
                errorLog.append(line);
            }

            log.debug("Finished algorithm to detect dead code in repository: {}", repository.getUrl());

            if (p.waitFor() != 0) {
                log.error("Error running algorithm to detect dead code in repository {}:\n{}", repository.getUrl(), errorLog);
                throw new AnalysisException("Error running algorithm to detect dead code.");
            }

            return outputLog.toString();
        } catch (IOException | InterruptedException ex) {
            log.error("Error running algorithm to detect dead code in repository {}:\n{}", repository.getUrl(), ex.getMessage());
            throw new AnalysisException("Error running algorithm to detect dead code: {}" + ex.getMessage());
        }

    }

    private Path createUDBFile(Repository repository, Path repositoryDir, String dataDir) {
        log.info("Creating UDB file for repository {}", repository.getUrl());

        final String CREATE_UND_SCRIPT = scriptsDir + "create_und.sh";
        final String UND_FILE = dataDir + String.format("%s-%s", repository.getOwner(), repository.getName());

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(CREATE_UND_SCRIPT, UND_FILE, repositoryDir.toString());
            processBuilder.environment().put("SCITOOLS_HOME", scitoolsHome);

            Process p = processBuilder.start();

            BufferedReader stdout = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader stderr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            String line;

            StringBuilder errorLog = new StringBuilder();

            while ((line = stdout.readLine()) != null) {
                System.out.println(line);
            }

            while ((line = stderr.readLine()) != null) {
                errorLog.append(line);
            }

            log.debug("Finished creation of UDB file for repository: {}", repository.getUrl());

            if (p.waitFor() != 0) {
                log.error("Error creating UDB file\n{}", errorLog);
                throw new AnalysisException("Error creating UDB file");
            }

            return Paths.get(UND_FILE + ".udb");
        } catch (IOException | InterruptedException ex) {
            log.error("Error creating UDB file\n{}", ex.getMessage());
            throw new AnalysisException("Error creating UDB file" + ex.getMessage());
        }
    }

    private Path cloneGitHubRepository(Repository repository) {
        log.info("Cloning repository {}", repository.getUrl());

        try {
            File repositoryDir = this.createRepositoryDirectory(repository.getOwner(), repository.getName());

            Git.cloneRepository()
                    .setBare(false)
                    .setURI(repository.getUrl())
                    .setDirectory(repositoryDir)
                    .setBranchesToClone(singletonList(BRANCH_TO_ANALYZE))
                    .setCloneSubmodules(false)
                    .call();

            // the analysis doesn't required the .git directory.
            removeDotGitDir(repositoryDir);
            new File(repositoryDir, repository.getName()).delete();

            return repositoryDir.toPath();
        } catch(GitAPIException | JGitInternalException | IOException ex) {
            log.error("Error downloading Github repository with message {}", ex.getMessage());
            throw new AnalysisException(ex.getMessage());
        }
    }

    private void removeDotGitDir(File repositoryDir) throws IOException {
        Path gitDir = Paths.get(repositoryDir.getAbsolutePath(), ".git");
        deleteSubDirectoryStructure(gitDir);
    }

    private File createRepositoryDirectory(String owner, String repository) throws IOException {
        String repositoriesDirectory = dataDir + REPOSITORIES_SUBDIR;

        Path path = Paths.get(repositoriesDirectory, owner + "/");

        if (Files.exists(path)) {
            deleteSubDirectoryStructure(path);
        }

        Files.createDirectories(path);

        return Paths.get(repositoriesDirectory, owner, repository).toFile();
    }

    private void deleteSubDirectoryStructure(Path path) throws IOException {
        Files.walk(path, FileVisitOption.FOLLOW_LINKS)
                .sorted(reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }
}