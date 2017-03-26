package com.wrpinheiro.deadcodedetection.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Created by wrpinheiro on 3/21/17.
 */
@JsonDeserialize(builder = Repository.RepositoryBuilder.class)
@Data
@Builder
public class Repository {
    private Long id;
    private String name;
    private AnalysisStatus status;
    private String errorMessage;

    /**
     * The time the repository was added
     */
    private Date createdAt;

    private GithubRepository githubRepository;

    /**
     * The time the repository was processed
     */
    private Date processedAt;

    private List<DeadCodeIssue> deadCodeIssues;

    public void setStatus(AnalysisStatus status) {
        this.status = status;

        if (!status.equals(AnalysisStatus.FAILED)) {
            this.errorMessage = null;
        }

        if (!status.equals(AnalysisStatus.COMPLETED)) {
            this.deadCodeIssues = null;
        }
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static final class RepositoryBuilder {
    }
}
