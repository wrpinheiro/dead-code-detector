package com.wrpinheiro.deadcodedetection.model;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author wrpinheiro
 */
@Data
public class AnalysisInformation {
    public enum Stage {
        STARTED, CLONING_REPO, CREATING_UDB_FILE, CHECKING_DEAD_CODE, CREATING_DEAD_CODE_ISSUES, DONE
    }

    /**
     * Date the analysis stared
     */
    private Date startedAt;
    /**
     * Date the analysis finished
     */
    private Date finishedAt;

    private Stage stage = Stage.STARTED;

    private String errorMessage;

    private List<DeadCodeIssue> deadCodeIssues;
}
