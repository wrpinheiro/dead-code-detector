package com.wrpinheiro.deadcodedetection.service.analysis;

import com.wrpinheiro.deadcodedetection.exceptions.AnalysisException;
import com.wrpinheiro.deadcodedetection.model.AnalysisInformation;
import com.wrpinheiro.deadcodedetection.model.AnalysisStatus;
import com.wrpinheiro.deadcodedetection.model.DeadCodeIssue;
import com.wrpinheiro.deadcodedetection.model.Repository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.wrpinheiro.deadcodedetection.model.AnalysisInformation.Stage.CHECKING_DEAD_CODE;
import static com.wrpinheiro.deadcodedetection.model.AnalysisInformation.Stage.CREATING_DEAD_CODE_ISSUES;
import static com.wrpinheiro.deadcodedetection.model.AnalysisInformation.Stage.CREATING_UDB_FILE;
import static com.wrpinheiro.deadcodedetection.model.AnalysisInformation.Stage.DONE;
import static java.util.Arrays.asList;

/**
 * @author wrpinheiro
 */
@Slf4j
@Service
public class AnalysisServiceImpl implements AnalysisService {

    @Value("${app.analyzer.dataDir}")
    private String dataDir;

    @Value("${app.analyzer.scriptsDir}")
    private String scriptsDir;

    @Value("${app.analyzer.scitoolsHome}")
    private String scitoolsHome;

    @Autowired
    private GithubService githubService;

    @Override
    @Async
    public void analyze(Repository repository) {
        log.info("Starting analysis for repository {}", repository.getUuid());

        AnalysisInformation analysisInformation = new AnalysisInformation();
        analysisInformation.setStartedAt(new Date());

        repository.setStatus(AnalysisStatus.PROCESSING);
        repository.setLastAnalysisInformation(analysisInformation);

        try {
            Path repositoryDir = githubService.cloneGitHubRepository(repository);
            Path udbFile = createUDBFile(repository, repositoryDir, dataDir);
            String deadCodeIssuesOutput = checkDeadCodeAnalysis(repository, udbFile);

            List<DeadCodeIssue> deadCodeIssues = parseDeadCodeIssues(repository, deadCodeIssuesOutput);

            analysisInformation.setFinishedAt(new Date());
            analysisInformation.setDeadCodeIssues(deadCodeIssues);
            analysisInformation.setStage(DONE);

            repository.setStatus(AnalysisStatus.COMPLETED);

            log.info("Finished analysis for repository {}", repository.getUuid());
        } catch (Exception ex) {
            log.info("Error analyzing repository {}", repository.getUuid());
            log.debug("Error analyzing repository", ex);
            analysisInformation.setErrorMessage(ex.getMessage());
            repository.setStatus(AnalysisStatus.FAILED);
        }
    }

    private Path createUDBFile(Repository repository, Path repositoryDir, String dataDir) {
        log.info("Creating UDB file for repository {}", repository.getUuid());
        repository.getLastAnalysisInformation().setStage(CREATING_UDB_FILE);

        final String UND_EXECUTABLE = new File(scitoolsHome, "und").getAbsolutePath();
        final String UDB_FILE = dataDir + String.format("%s.udb", repository.getUuid());

        try {
            log.debug(String.join(" ", UND_EXECUTABLE, "create", "-db", UDB_FILE, "-languages",
                    repository.getGithubRepository().getLanguage().getStrValue(), "add", repositoryDir.toString(),
                    "analyze"));

            ProcessUtils.ProcessOutput output = ProcessUtils.runProcess(ProcessUtils.ProcessCommand.builder()
                    .commands(asList(UND_EXECUTABLE, "create", "-db", UDB_FILE, "-languages", repository
                                    .getGithubRepository().getLanguage().getStrValue(), "add", repositoryDir.toString(),
                            "analyze")).timeout(60).build());

            if (output.getExitCode() != 0) {
                log.info("Error creating UDB file for repository: {}.", repository.getUuid());

                String logs = String.format("\n\nStdout: %s\n\nStderr: %s\n\n", output.getStdout(), output.getStderr());
                log.error("Error creating UDB file\n{}", logs);

                throw new AnalysisException("Error creating UDB file: " + UDB_FILE);
            }

            log.info("Finished creation of UDB file for repository: {}", repository.getUuid());

            return Paths.get(UDB_FILE);
        } catch (Exception ex) {
            log.error("Error creating UDB file {}. Message: {}", UDB_FILE, ex.getMessage());
            throw new AnalysisException("Error creating UDB file" + UDB_FILE + ". Message: " + ex.getMessage(), ex);
        }
    }

    private String checkDeadCodeAnalysis(Repository repository, Path udbFile) {
        log.info("Running script to find dead code in repository {}", repository.getUuid());
        repository.getLastAnalysisInformation().setStage(CHECKING_DEAD_CODE);

        final String UPERL_FILE = scitoolsHome + "/uperl";
        final String UNUSED_CODE_SCRIPT = scriptsDir + "/acjf_unused_modified.pl";

        try {
            ProcessUtils.ProcessOutput output = ProcessUtils.runProcess(ProcessUtils.ProcessCommand.builder()
                    .commands(asList(UPERL_FILE, UNUSED_CODE_SCRIPT, "-db",
                            udbFile.toAbsolutePath().toString(), "-byKind")).timeout(60).build());

            log.debug("Finished algorithm to detect dead code in repository: {}", repository.getUuid());

            if (output.getExitCode() != 0) {
                log.error("Error running algorithm to detect dead code in repository {}:\n{}", repository.getUuid(),
                        output.getStderr());
                throw new AnalysisException("Error running algorithm to detect dead code.");
            }

            return output.getStdout();
        } catch (Exception ex) {
            log.error("Error running algorithm to detect dead code in repository {}:\n{}", repository.getUuid(),
                    ex.getMessage());
            throw new AnalysisException("Error running algorithm to detect dead code: {}" + ex.getMessage(), ex);
        }
    }

    /**
     * The kind of dead code starts with an "@" and the information about the dead code (the file, lines, etc) and in a
     * dot comma separated string
     *
     * @param repository
     * @param deadCodeOutput
     */
    private List<DeadCodeIssue> parseDeadCodeIssues(Repository repository, String deadCodeOutput) {
        log.info("Creating instances of dead code issues for repository {}", repository.getUuid());

        Pattern filenamePattern = Pattern.compile(String.format(".*%s/%s/(.*)", repository.getUuid(), repository
                .getGithubRepository().getName()));

        repository.getLastAnalysisInformation().setStage(CREATING_DEAD_CODE_ISSUES);

        List<DeadCodeIssue> deadCodeIssues = new ArrayList<>();

        String lastType = "";
        String line;
        for (String str: deadCodeOutput.split("\\?")) {
            line = str.trim();
            if (line.startsWith("@")) {
                lastType = line.substring(1);
            } else if (!line.equals("")){
                String[] location = line.split(";");

                deadCodeIssues.add(deadCodeLocationToInstance(lastType, location, filenamePattern));
            }
        }

        deadCodeIssues.sort(Comparator.comparing(DeadCodeIssue::getFilename));

        return deadCodeIssues;
    }

    private DeadCodeIssue deadCodeLocationToInstance(String kind, String[] location, Pattern filenamePattern) {
        String filename = location[2].trim();
        Matcher filenameMatcher = filenamePattern.matcher(filename);

        if (filenameMatcher.matches()) {
            filename = filenameMatcher.group(1);
        }

        return DeadCodeIssue.builder()
                .kind(kind)
                .filename(filename)
                .fromLine(Integer.valueOf(location[4].trim()))
                .toLine(Integer.valueOf(location[5].trim()))
                .ref(location[0].trim())
                .build();
    }
}
