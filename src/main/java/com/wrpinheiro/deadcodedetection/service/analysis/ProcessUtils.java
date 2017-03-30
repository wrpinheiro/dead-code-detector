package com.wrpinheiro.deadcodedetection.service.analysis;

import lombok.Builder;
import lombok.Data;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * A utility class to deal with process.
 *
 * @author wrpinheiro
 */
public class ProcessUtils {
    @Data
    @Builder
    public static class ProcessCommand {
        /**
         * time in SECONDS.
         */
        private long timeout;
        private List<String> commands;
    }

    @Data
    @Builder
    public static class ProcessOutput {
        private int exitCode;
        private String stdout;
        private String stderr;
    }

    /**
     * Execute a shell command in an external process.
     *
     * @param processCommand the command to be executed
     * @return the output information for the process
     * @throws InterruptedException when the thread executing the process was interrupted
     * @throws TimeoutException when the process times out
     * @throws IOException when the command could not be found
     */
    public static ProcessOutput runProcess(final ProcessCommand processCommand)
            throws InterruptedException, TimeoutException, IOException {
        final ByteArrayOutputStream boa = new ByteArrayOutputStream();

        final ProcessResult processResult = new ProcessExecutor().command(processCommand.getCommands())
                .timeout(processCommand.getTimeout(), TimeUnit.SECONDS)
                .readOutput(true).redirectError(boa).execute();

        final String errorOutput = new String(boa.toByteArray(), "UTF-8");

        return ProcessOutput.builder().exitCode(processResult.getExitValue())
            .stdout(processResult.getOutput().getString())
            .stderr(errorOutput).build();
    }
}
