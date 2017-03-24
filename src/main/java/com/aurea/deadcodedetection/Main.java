package com.aurea.deadcodedetection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by wrpinheiro on 3/24/17.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        final String CREATE_UND_SCRIPT = "/Users/wrpinheiro/mystuff/crossover/dead-code-detection/scripts/" + "create_und.sh";
        final String UND_FILE = "/Users/wrpinheiro/mystuff/crossover/dead-code-detection/examples/simple-example/simple.udb";

        System.out.println(CREATE_UND_SCRIPT);
        System.out.println(UND_FILE);

        ProcessBuilder processBuilder = new ProcessBuilder(CREATE_UND_SCRIPT, UND_FILE, "/Users/wrpinheiro/mystuff/crossover/dead-code-detection/examples/simple-example");
        processBuilder.environment().put("SCITOOLS_PATH", "/Applications/scitools/bin/macosx/");

        Process p = processBuilder.start();

        BufferedReader output = new BufferedReader(new InputStreamReader(p.getInputStream()));
        BufferedReader error = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        String outputLine;
        String errorLine;

        while ((outputLine = output.readLine()) != null)
            System.out.println(outputLine);

        while ((errorLine = error.readLine()) != null)
            System.out.println(errorLine);

        System.out.println("====> Process output: ");
        System.out.println(p.exitValue());

    }
}
