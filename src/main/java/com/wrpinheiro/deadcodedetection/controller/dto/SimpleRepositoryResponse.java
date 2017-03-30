package com.wrpinheiro.deadcodedetection.controller.dto;

import com.wrpinheiro.deadcodedetection.model.GithubRepository;
import com.wrpinheiro.deadcodedetection.model.Repository;
import com.wrpinheiro.deadcodedetection.model.RepositoryStatus;
import lombok.Data;

import java.util.Date;

/**
 * A representation of repository without the list of dead code issues.
 *
 * @author wrpinheiro
 */
@Data
public class SimpleRepositoryResponse {
    private String uuid;
    private GithubRepository githubRepository;
    private RepositoryStatus status;
    private Date createdAt;
    private Date lastAnalysisRequested;
    private SimpleAnalysisInformationResponse lastAnalysisInformation;

    public SimpleRepositoryResponse() {
    }

    /**
     * Create the Simple Repository and fill its properties with values from the attribute repository.
     */
    public SimpleRepositoryResponse(final Repository repository) {
        this.uuid = repository.getUuid();
        this.githubRepository = repository.getGithubRepository();
        this.status = repository.getStatus();
        this.createdAt = repository.getCreatedAt();
        this.lastAnalysisRequested = repository.getLastAnalysisRequested();
        this.lastAnalysisInformation = new SimpleAnalysisInformationResponse(repository.getLastAnalysisInformation());
    }
}
