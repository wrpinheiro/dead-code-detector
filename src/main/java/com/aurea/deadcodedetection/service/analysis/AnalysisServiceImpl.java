package com.aurea.deadcodedetection.service.analysis;

import com.aurea.deadcodedetection.model.AnalysisStatus;
import com.aurea.deadcodedetection.model.Repository;
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
            createUDBFile(repository, repositoryDir, dataDir);

        } catch (AnalysisException ex) {
            repository.setErrorMessage(ex.getMessage());
            repository.setStatus(AnalysisStatus.FAILED);
        }
    }

    private void createUDBFile(Repository repository, Path repositoryDir, String dataDir) {
        log.info("Cloning UDB file for repository {}", repository.getUrl());

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

            if (p.exitValue() != 0) {
                log.error("Error creating UDB file\n{}", errorLog);
                throw new AnalysisException("Error creating UDB file");
            }
        } catch (IOException ex) {
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
                .peek(System.out::println)
                .forEach(File::delete);
    }
}
