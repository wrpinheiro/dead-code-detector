package com.wrpinheiro.deadcodedetection.service.analysis;

import static com.wrpinheiro.deadcodedetection.model.AnalysisInformation.Stage.CHECKING_DEAD_CODE;
import static com.wrpinheiro.deadcodedetection.model.AnalysisInformation.Stage.CREATING_DEAD_CODE_ISSUES;
import static com.wrpinheiro.deadcodedetection.model.AnalysisInformation.Stage.CREATING_UDB_FILE;
import static com.wrpinheiro.deadcodedetection.model.AnalysisInformation.Stage.DONE;
import static java.util.Arrays.asList;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import com.wrpinheiro.deadcodedetection.exceptions.AnalysisException;
import com.wrpinheiro.deadcodedetection.model.AnalysisInformation;
import com.wrpinheiro.deadcodedetection.model.DeadCodeIssue;
import com.wrpinheiro.deadcodedetection.model.Repository;
import com.wrpinheiro.deadcodedetection.model.RepositoryStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of analysis services.
 *
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

    @Value("#{'${app.analyzer.kindBlacklist}'.toLowerCase().split(';')}")
    private List<String> kindBlacklist;

    @Value("#{'${app.analyzer.kindWhitelist}'.toLowerCase().split(';')}")
    private List<String> kindWhitelist;

    @Value("#{'${app.analyzer.refBlackList}'.toLowerCase().split(';')}")
    private List<String> refBlackList;

    @Autowired
    private GithubService githubService;

    @Override
    @Async
    public void analyze(final Repository repository) {
        log.info("Starting analysis for repository {}", repository.getUuid());

        final AnalysisInformation analysisInformation = new AnalysisInformation();
        analysisInformation.setStartedAt(new Date());

        repository.setStatus(RepositoryStatus.PROCESSING);
        repository.setLastAnalysisInformation(analysisInformation);

        try {
            final Path repositoryDir = githubService.cloneGitHubRepository(repository);
            final Path udbFile = createUDBFile(repository, repositoryDir, dataDir);
            final String deadCodeIssuesOutput = checkDeadCodeAnalysis(repository, udbFile);

            final List<DeadCodeIssue> deadCodeIssues = parseDeadCodeIssues(repository, deadCodeIssuesOutput);

            analysisInformation.setFinishedAt(new Date());
            analysisInformation.setDeadCodeIssues(deadCodeIssues);
            analysisInformation.setStage(DONE);

            repository.setStatus(RepositoryStatus.COMPLETED);

            log.info("Finished analysis for repository {}", repository.getUuid());
        } catch (Exception ex) {
            log.info("Error analyzing repository {}", repository.getUuid());
            log.debug("Error analyzing repository", ex);
            analysisInformation.setErrorMessage(ex.getMessage());
            repository.setStatus(RepositoryStatus.FAILED);
        }
    }

    private Path createUDBFile(final Repository repository, final Path repositoryDir, final String dataDir) {
        log.info("Creating UDB file for repository {}", repository.getUuid());
        repository.getLastAnalysisInformation().setStage(CREATING_UDB_FILE);

        final String UND_EXECUTABLE = new File(scitoolsHome, "und").getAbsolutePath();
        final String UDB_FILE = dataDir + String.format("%s.udb", repository.getUuid());

        try {
            log.debug(String.join(" ", UND_EXECUTABLE, "create", "-db", UDB_FILE, "-languages",
                    repository.getGithubRepository().getLanguage().getStrValue(), "add", repositoryDir.toString(),
                    "analyze"));

            final ProcessUtils.ProcessOutput output = ProcessUtils.runProcess(ProcessUtils.ProcessCommand.builder()
                    .commands(asList(UND_EXECUTABLE, "create", "-db", UDB_FILE, "-languages", repository
                                    .getGithubRepository().getLanguage().getStrValue(), "add", repositoryDir.toString(),
                            "analyze")).timeout(60).build());

            if (output.getExitCode() != 0) {
                log.info("Error creating UDB file for repository: {}.", repository.getUuid());

                final String logs = String.format("\n\nStdout: %s\n\nStderr: %s\n\n", output.getStdout(),
                        output.getStderr());
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

    private String checkDeadCodeAnalysis(final Repository repository, final Path udbFile) {
        log.info("Running script to find dead code in repository {}", repository.getUuid());
        repository.getLastAnalysisInformation().setStage(CHECKING_DEAD_CODE);

        final String UPERL_FILE = scitoolsHome + "/uperl";
        final String UNUSED_CODE_SCRIPT = scriptsDir + "/acjf_unused_modified.pl";

        try {
            final ProcessUtils.ProcessOutput output = ProcessUtils.runProcess(ProcessUtils.ProcessCommand.builder()
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
     * <p>Parse a string representing the dead code issues.
     *
     * The kind of dead code starts with an "@" and the information about the dead code (the file, lines, etc) and in a
     * dot comma separated string.</p>
     *
     * @param repository the repository related to the dead code issues
     * @param deadCodeOutput the string representing the dead code issues
     */
    private List<DeadCodeIssue> parseDeadCodeIssues(final Repository repository, final String deadCodeOutput) {
        log.info("Creating instances of dead code issues for repository {}", repository.getUuid());

        log.info(deadCodeOutput);

        final Pattern filenamePattern = Pattern.compile(String.format(".*%s/%s/(.*)", repository.getUuid(), repository
                .getGithubRepository().getName()));
        final List<DeadCodeIssue> deadCodeIssues = new ArrayList<>();

        repository.getLastAnalysisInformation().setStage(CREATING_DEAD_CODE_ISSUES);

        String lastType = "";
        String line;

        for (final String str: deadCodeOutput.split("\\?")) {
            line = str.trim();
            if (isNotEmpty(line)) {
                if (line.charAt(0) == '@') {
                    lastType = line.substring(1);
                } else {
                    final String[] location = line.split(";");

                    deadCodeIssues.add(deadCodeLocationToInstance(lastType, location, filenamePattern));
                }
            }
        }

        return filterValidIssuesAndSort(deadCodeIssues);
    }

    /**
     * <p>Checks if a dead code issue is valid based on kind and ref.
     *
     * An issue is considered valid:
     * * kind does not contain any word in the kind blacklist
     * * kind contains at least one word in the kind whitelis
     * * ref does not contain any word in the ref blacklist.</p>
     *
     * @param deadCodeIssue the issue to be checked
     * @return true if the issue is valid or false otherwise
     */
    private boolean isValidKindAndRef(final DeadCodeIssue deadCodeIssue) {
        final String kind = deadCodeIssue.getKind().toLowerCase();
        final String ref = deadCodeIssue.getRef().toLowerCase();

        return !kindBlacklist.stream().anyMatch(kindBlacklisted -> kind.contains(kindBlacklisted))
                && kindWhitelist.stream().anyMatch(kindWhitelisted -> kind.contains(kindWhitelisted))
                && !refBlackList.stream().anyMatch(refBlacklisted -> ref.contains(refBlacklisted));
    }

    private List<DeadCodeIssue> filterValidIssuesAndSort(final List<DeadCodeIssue> deadCodeIssues) {
        return deadCodeIssues.stream()
                .filter(this::isValidKindAndRef)
                .sorted(comparing(DeadCodeIssue::getFilename)).collect(toList());
    }

    private DeadCodeIssue deadCodeLocationToInstance(final String kind, final String[] location,
                                                     final Pattern filenamePattern) {
        String filename = location[2].trim();
        final Matcher filenameMatcher = filenamePattern.matcher(filename);

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
