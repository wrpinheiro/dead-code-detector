package com.wrpinheiro.deadcodedetection;

import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by wrpinheiro on 3/24/17.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        try {
            ByteArrayOutputStream boa = new ByteArrayOutputStream();

            ProcessResult processResult = new ProcessExecutor().command("/Applications/scitools/bin/macosx/und", "create", "-db",
                    "/Users/wrpinheiro/.deadCodeDetection/wrpinheiro-jgraphlib.udb", "-languages", "java", "add",
                    "/Users/wrpinheiro/.deadCodeDetection/repos/wrpinheiro/jgraphlib", "analyze")
                    .readOutput(true).redirectError(boa).execute();

        } catch (TimeoutException | InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}

