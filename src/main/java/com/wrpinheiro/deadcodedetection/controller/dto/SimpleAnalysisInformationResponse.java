package com.wrpinheiro.deadcodedetection.controller.dto;

import com.wrpinheiro.deadcodedetection.model.AnalysisInformation;
import com.wrpinheiro.deadcodedetection.model.AnalysisInformation.Stage;
import lombok.Data;

import java.util.Date;

/**
 * A response model used to show the analysis information suppressing the list of dead code issues.
 *
 * @author wrpinheiro
 */
@Data
public class SimpleAnalysisInformationResponse {
    private Date startedAt;
    private Date finishedAt;
    private Stage stage;
    private String errorMessage;

    /**
     * Create a simplified representation for the analysis information.
     * @param analysisInformation the complete analysis information model.
     */
    public SimpleAnalysisInformationResponse(final AnalysisInformation analysisInformation) {
        this.startedAt = analysisInformation.getStartedAt();
        this.finishedAt = analysisInformation.getFinishedAt();
        this.stage = analysisInformation.getStage();
        this.errorMessage = analysisInformation.getErrorMessage();
    }
}
