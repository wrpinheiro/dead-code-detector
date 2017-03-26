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
 * A utility class to deal with process
 *
 * Created by wrpinheiro on 3/26/17.
 */
public class ProcessUtils {
    @Data
    @Builder
    public static class ProcessCommand {
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
     * @return the output of the process
     * @throws InterruptedException
     * @throws TimeoutException
     * @throws IOException
     */
    public static ProcessOutput runProcess(ProcessCommand processCommand)
            throws InterruptedException, TimeoutException, IOException {
        ByteArrayOutputStream boa = new ByteArrayOutputStream();

        ProcessResult processResult = new ProcessExecutor().command(processCommand.getCommands())
                .timeout(processCommand.getTimeout(), TimeUnit.SECONDS)
                .readOutput(true).redirectError(boa).execute();

        String errorOutput = new String(boa.toByteArray(), "UTF-8");

        return ProcessOutput.builder().exitCode(processResult.getExitValue())
            .stdout(processResult.getOutput().getString())
            .stderr(errorOutput).build();
    }
}
