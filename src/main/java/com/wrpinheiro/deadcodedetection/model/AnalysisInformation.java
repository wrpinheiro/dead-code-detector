package com.wrpinheiro.deadcodedetection.model;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Created by wrpinheiro on 3/27/17.
 */
@Data
public class AnalysisInformation {
    /**
     * Date the analysis stared
     */
    private Date startedAt;
    /**
     * Date the analysis finished
     */
    private Date finishedAt;

    private List<DeadCodeIssue> deadCodeIssues;
}
