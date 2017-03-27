package com.wrpinheiro.deadcodedetection.controller.dto;

import com.wrpinheiro.deadcodedetection.model.AnalysisStatus;
import com.wrpinheiro.deadcodedetection.model.GithubRepository;
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
    private GithubRepository githubRepository;
    private AnalysisStatus status;
    private Date createdAt;
    private Date lastAnalysisRequested;
    private SimpleAnalysisInformationResponse lastAnalysisInformation;

    public SimpleRepositoryResponse(Repository repository) {
        this.id = repository.getId();
        this.name = repository.getName();
        this.githubRepository = repository.getGithubRepository();
        this.status = repository.getStatus();
        this.createdAt = repository.getCreatedAt();
        this.lastAnalysisRequested = repository.getLastAnalysisRequested();
        this.lastAnalysisInformation = new SimpleAnalysisInformationResponse(repository.getLastAnalysisInformation());
    }
}
