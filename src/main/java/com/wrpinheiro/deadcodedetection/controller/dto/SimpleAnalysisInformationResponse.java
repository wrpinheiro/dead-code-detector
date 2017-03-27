package com.wrpinheiro.deadcodedetection.controller.dto;

import com.wrpinheiro.deadcodedetection.model.AnalysisInformation;
import lombok.Data;

import java.util.Date;

/**
 * Created by wrpinheiro on 3/27/17.
 */
@Data
public class SimpleAnalysisInformationResponse {
    private Date startedAt;
    private Date finishedAt;

    public SimpleAnalysisInformationResponse(AnalysisInformation analysisInformation) {
        this.startedAt = analysisInformation.getStartedAt();
        this.finishedAt = analysisInformation.getFinishedAt();
    }
}
