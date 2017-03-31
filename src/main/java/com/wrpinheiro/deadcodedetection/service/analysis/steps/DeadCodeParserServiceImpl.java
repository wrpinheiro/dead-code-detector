package com.wrpinheiro.deadcodedetection.service.analysis.steps;

import static com.wrpinheiro.deadcodedetection.model.AnalysisInformation.Stage.CREATING_DEAD_CODE_ISSUES;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import com.wrpinheiro.deadcodedetection.model.AnalysisInformation;
import com.wrpinheiro.deadcodedetection.model.DeadCodeIssue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation methods for DeadCodeParserService.
 *
 * @author wrpinheiro
 */
@Slf4j
@Service
public class DeadCodeParserServiceImpl implements DeadCodeParserService {
    /**
     * <p>Parse a string representing the dead code issues.
     *
     * The kind of dead code starts with an "@" and the information about the dead code (the file, lines, etc) and in a
     * dot comma separated string.</p>
     *
     * @param uuid the uuid of the repository
     * @param githubRepositoryName the name of the Github repository
     * @param analysisInformation the analysis information to track information of the process
     * @param deadCodeOutput the string representing the dead code issues
     */
    public List<DeadCodeIssue> parse(final String uuid, final String githubRepositoryName,
                                     final AnalysisInformation analysisInformation, final String deadCodeOutput) {
        analysisInformation.setStage(CREATING_DEAD_CODE_ISSUES);

        log.info("Creating instances of dead code issues for repository {}", uuid);

        log.debug(deadCodeOutput);

        final Pattern filenamePattern = Pattern.compile(String.format(".*%s/%s/(.*)", uuid, githubRepositoryName));

        final List<DeadCodeIssue> deadCodeIssues = new ArrayList<>();

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

        return deadCodeIssues;
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
                .column(Integer.valueOf(location[5].trim()))
                .toLine(Integer.valueOf(location[6].trim()))
                .ref(location[0].trim())
                .build();
    }
}
