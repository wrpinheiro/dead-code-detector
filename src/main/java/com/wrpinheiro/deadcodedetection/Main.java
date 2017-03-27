package com.wrpinheiro.deadcodedetection;

import com.wrpinheiro.deadcodedetection.service.analysis.ProcessUtils;

import java.io.IOException;

import static java.util.Arrays.asList;

/**
 * Created by wrpinheiro on 3/24/17.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        try {
            // ProcessInitException quando executavel nao é encontrado
            // TimeoutException quando processo dá timeout
            int exitCode = 0;

            ProcessUtils.ProcessOutput output = ProcessUtils.runProcess(ProcessUtils.ProcessCommand.builder()
                    .commands(asList("/Applications/scitools/bin/macosx/und", "create", "-db",
                    "/Users/wrpinheiro/.deadCodeDetection/jgraphlib-1.0.udb", "-languages", "java", "add",
                    "/Users/wrpinheiro/.deadCodeDetection/repos/jgraphlib-1.0/", "analyze")).timeout(60).build());

            System.out.println("===>");
            System.out.println("Exit code: " + exitCode);
            System.out.println("stdout: " + output.getStdout());
            System.out.println("stderr: " + output.getStderr());
            System.out.println("<===");

        } catch (Exception ex) { //TimeoutException | InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}

