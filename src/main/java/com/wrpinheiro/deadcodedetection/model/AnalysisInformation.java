package com.wrpinheiro.deadcodedetection.model;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Keep information about the analysis process.
 *
 * @author wrpinheiro
 */
@Data
public class AnalysisInformation {
    /**
     * The stage of the analysis process.
     */
    public enum Stage {
        STARTED, CLONING_REPO, CREATING_UDB_FILE, CHECKING_DEAD_CODE, CREATING_DEAD_CODE_ISSUES, DONE
    }

    private Date startedAt;

    private Date finishedAt;

    private Stage stage = Stage.STARTED;

    private String errorMessage;

    private List<DeadCodeIssue> deadCodeIssues;
}
