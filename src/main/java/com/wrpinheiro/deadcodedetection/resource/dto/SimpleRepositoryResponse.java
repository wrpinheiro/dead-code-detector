package com.wrpinheiro.deadcodedetection.resource.dto;

import com.wrpinheiro.deadcodedetection.model.AnalysisStatus;
import com.wrpinheiro.deadcodedetection.model.Language;
import com.wrpinheiro.deadcodedetection.model.Repository;
import lombok.Data;

import java.util.Date;

/**
 * Created by wrpinheiro on 3/21/17.
 */
@Data
public class SimpleRepositoryResponse {
    private Long id;
    private String name;
    private String description;
    private Language language;
    private AnalysisStatus status;
    private String errorMessage;
    private Date createdAt;
    private Date processedAt;
    private String gitHubUrl;

    public SimpleRepositoryResponse(Repository repository) {
        this.id = repository.getId();
        this.name = repository.getName();
        this.description = repository.getRepositoryDescription();
        this.language = repository.getLanguage();
        this.status = repository.getStatus();
        this.errorMessage = repository.getErrorMessage();
        this.createdAt = repository.getCreatedAt();
        this.processedAt = repository.getProcessedAt();
        this.gitHubUrl = repository.getUrl();
    }
}
