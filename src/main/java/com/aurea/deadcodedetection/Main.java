package com.aurea.deadcodedetection;

import com.aurea.deadcodedetection.model.DeadCodeIssue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by wrpinheiro on 3/24/17.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        List<String> listOfStrings = Files.readAllLines(Paths.get("/Users/wrpinheiro/mystuff/crossover/dead-code-detection/deadcode.txt"));
        String output = listOfStrings.get(0);

        String lastType = "";
        for (String s: output.split("\\?")) {
            if (s.startsWith("@")) {
                lastType = s.substring(1);
            } else if (!s.trim().equals("")){
                String[] location = s.split(";");

                DeadCodeIssue deadCodeIssue = parseDeadCodeIssue(lastType, location);

                System.out.println(deadCodeIssue);
            }
        }
    }

    private static DeadCodeIssue parseDeadCodeIssue(String kind, String[] location) {
        return DeadCodeIssue.builder()
                .kind(kind)
                .filename(location[2].trim())
                .fromLine(Integer.valueOf(location[4].trim()))
                .toLine(Integer.valueOf(location[5].trim()))
                .ref(location[0].trim())
                .build();
    }
}


