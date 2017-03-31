package com.wrpinheiro.deadcodedetection.service.analysis.steps;

import static com.wrpinheiro.deadcodedetection.model.AnalysisInformation.Stage.CREATING_UDB_FILE;
import static java.util.Arrays.asList;

import com.wrpinheiro.deadcodedetection.exceptions.AnalysisException;
import com.wrpinheiro.deadcodedetection.model.AnalysisInformation;
import com.wrpinheiro.deadcodedetection.model.GithubRepository;
import com.wrpinheiro.deadcodedetection.service.analysis.util.ProcessUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Implementation services to execute Scitools UND command.
 *
 * @author wrpinheiro
 */
@Slf4j
@Service
public class UnderstandServiceImpl implements UnderstandService {
    @Value("${app.analyzer.dataDir}")
    private String dataDir;

    @Value("${app.analyzer.scitoolsHome}")
    private String scitoolsHome;

    /**
     * @see UnderstandService#createUDBFile(String, GithubRepository, AnalysisInformation, Path).
     */
    public Path createUDBFile(final String uuid, final GithubRepository githubRepository,
                              final AnalysisInformation analysisInformation, final Path repositoryDir) {

        log.info("Creating UDB file for repository {}", uuid);
        analysisInformation.setStage(CREATING_UDB_FILE);

        final String UDB_FILE = dataDir + String.format("%s.udb", uuid);

        try {
            final List<String> undShellCommand = getUndCommand(githubRepository, repositoryDir, UDB_FILE);

            final ProcessUtils.ProcessCommand undCommand = ProcessUtils.ProcessCommand.builder().commands(
                    undShellCommand).timeout(60).build();
            final ProcessUtils.ProcessOutput output = ProcessUtils.runProcess(undCommand);

            if (output.getExitCode() != 0) {
                log.info("Error creating UDB file for repository: {}.", uuid);

                final String logs = String.format("\n\nStdout: %s\n\nStderr: %s\n\n", output.getStdout(),
                        output.getStderr());
                log.error("Error creating UDB file\n{}", logs);

                throw new AnalysisException("Error creating UDB file: " + UDB_FILE);
            }

            log.info("Finished creation of UDB file for repository: {}", uuid);

            return Paths.get(UDB_FILE);
        } catch (Exception ex) {
            log.error("Error creating UDB file {}. Message: {}", UDB_FILE, ex.getMessage());
            throw new AnalysisException("Error creating UDB file" + UDB_FILE + ". Message: " + ex.getMessage(), ex);
        }
    }

    private String getUndPath() {
        return new File(scitoolsHome, "und").getAbsolutePath();
    }

    private List<String> getUndCommand(final GithubRepository githubRepository, final Path repositoryDir,
                                       final String udbFile) {
        return asList(
                getUndPath(),
                "create", "-db", udbFile,
                "-languages", githubRepository.getLanguage().getStrValue(),
                "add", repositoryDir.toString(),
                "analyze");
    }
}
