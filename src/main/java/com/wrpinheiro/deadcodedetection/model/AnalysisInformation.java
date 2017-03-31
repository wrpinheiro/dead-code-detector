package com.wrpinheiro.deadcodedetection.model;

import static com.wrpinheiro.deadcodedetection.model.AnalysisInformation.Stage.DONE;
import static com.wrpinheiro.deadcodedetection.model.AnalysisInformation.Stage.STARTED;

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
        STARTED, CLONING_REPO, CREATING_UDB_FILE, CHECKING_DEAD_CODE, CREATING_DEAD_CODE_ISSUES,
        FILTERING_AND_SORTING_ISSUES, DONE
    }

    private Date startedAt;

    private Date finishedAt;

    private Stage stage = STARTED;

    private String errorMessage;

    private List<DeadCodeIssue> deadCodeIssues;

    /**
     * Set attributes to define the start of the analysis process.
     */
    public void startAnalysis() {
        setStartedAt(new Date());
        setStage(STARTED);
    }

    /**
     * Set attributes to define the end of the analysis process and the issues found.
     *
     * @param deadCodeIssues issues found
     */
    public void finishAnalysis(final List<DeadCodeIssue> deadCodeIssues) {
        setFinishedAt(new Date());
        setDeadCodeIssues(deadCodeIssues);
        setStage(DONE);
    }

}
