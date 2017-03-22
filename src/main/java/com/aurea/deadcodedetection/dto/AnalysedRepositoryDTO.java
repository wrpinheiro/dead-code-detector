package com.aurea.deadcodedetection.dto;

import com.aurea.deadcodedetection.model.AnalysisStatus;
import com.aurea.deadcodedetection.model.Repository;
import lombok.Data;

import java.util.Date;

/**
 * Created by wrpinheiro on 3/21/17.
 */
@Data
public class AnalysedRepositoryDTO {
    private Long id;
    private String name;
    private String description;
    private AnalysisStatus status;
    private Date createdAt;
    private Date processedAt;
    private String gitHubUrl;

    public AnalysedRepositoryDTO(Repository repository) {
        this.id = repository.getId();
        this.name = repository.getRepositoryName();
        this.description = repository.getRepositoryDescription();
        this.status = repository.getStatus();
        this.createdAt = repository.getCreatedAt();
        this.processedAt = repository.getProcessedAt();
        this.gitHubUrl = repository.getRepositoryUrl();
    }
}
