package com.wrpinheiro.deadcodedetection.service.analysis.algorithms;

import static com.wrpinheiro.deadcodedetection.model.AnalysisInformation.Stage.CHECKING_DEAD_CODE;
import static java.util.Arrays.asList;

import com.wrpinheiro.deadcodedetection.exceptions.AnalysisException;
import com.wrpinheiro.deadcodedetection.model.AnalysisInformation;
import com.wrpinheiro.deadcodedetection.service.analysis.util.ProcessUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

/**
 * Algorithm that finds dead code references. This implementation is actually an interface to run the script
 * acjf_unused_modified.pl perl script. The result of the execution is returned as a string to be parsed.
 *
 * @author wrpinheiro
 */
@Slf4j
@Service
public class DeadCodeAnalyzer {
    @Value("${app.analyzer.scriptsDir}")
    private String scriptsDir;

    @Value("${app.analyzer.scitoolsHome}")
    private String scitoolsHome;

    /**
     * Run the perl script acjf_unused_modified.pl to find dead code and return the algorithm output to be
     * parsed.
     *
     * @param uuid the uuid of the repository
     * @param analysisInformation an instance used to track the execution
     * @param udbFile the UDB file previously generated with scitools und command.
     * @return a string representing the dead code found
     */
    public String findDeadCodeIssues(final String uuid, final AnalysisInformation analysisInformation,
                                      final Path udbFile) {
        log.info("Running script to find dead code in repository {}", uuid);
        analysisInformation.setStage(CHECKING_DEAD_CODE);

        final String UPERL_FILE = scitoolsHome + "/uperl";
        final String UNUSED_CODE_SCRIPT = scriptsDir + "/acjf_unused_modified.pl";

        try {
            final ProcessUtils.ProcessOutput output = ProcessUtils.runProcess(ProcessUtils.ProcessCommand.builder()
                    .commands(asList(UPERL_FILE, UNUSED_CODE_SCRIPT, "-db",
                            udbFile.toAbsolutePath().toString(), "-byKind")).timeout(60).build());

            log.debug("Finished algorithms to detect dead code in repository: {}", uuid);

            if (output.getExitCode() != 0) {
                log.error("Error running algorithms to detect dead code in repository {}:\n{}", uuid,
                        output.getStderr());
                throw new AnalysisException("Error running algorithms to detect dead code.");
            }

            return output.getStdout();
        } catch (Exception ex) {
            log.error("Error running algorithms to detect dead code in repository {}:\n{}", uuid,
                    ex.getMessage());
            throw new AnalysisException("Error running algorithms to detect dead code: {}" + ex.getMessage(), ex);
        }
    }


}
