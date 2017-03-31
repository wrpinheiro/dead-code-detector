package com.wrpinheiro.deadcodedetection.service.analysis.steps;

import com.wrpinheiro.deadcodedetection.model.AnalysisInformation;
import com.wrpinheiro.deadcodedetection.model.DeadCodeIssue;

import java.util.List;

/**
 * Service to filter and sort a list of dead code issues.
 *
 * @author wrpinheiro
 */
public interface DeadCodeIssueFilterService {
    /**
     * Filter a list of dead code issue.
     *
     * @param uuid the uuid of repository
     * @param analysisInformation the analysis information to keep track of the analysis
     * @param deadCodeIssues a list of dead code issues
     * @return a new list of dead code issues filtered and sorted
     */
    List<DeadCodeIssue> filterValidIssuesAndSort(String uuid, AnalysisInformation analysisInformation,
                                                        List<DeadCodeIssue> deadCodeIssues);
}
