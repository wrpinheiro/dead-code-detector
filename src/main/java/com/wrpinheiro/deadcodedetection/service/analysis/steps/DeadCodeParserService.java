package com.wrpinheiro.deadcodedetection.service.analysis.steps;

import com.wrpinheiro.deadcodedetection.model.AnalysisInformation;
import com.wrpinheiro.deadcodedetection.model.DeadCodeIssue;

import java.util.List;

/**
 * Service to parse a string returned by the acjf_unused_modified.pl algorithm.
 *
 * @author wrpinheiro
 */
public interface DeadCodeParserService {
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
    List<DeadCodeIssue> parse(String uuid, String githubRepositoryName, AnalysisInformation analysisInformation,
                              String deadCodeOutput);
}
