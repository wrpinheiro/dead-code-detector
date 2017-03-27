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
     * Date when the repository was added
     */
    private Date createdAt;

    /**
     * Date when the user requested the repository analysis (note that this is not the same
     * date/time the analysis started, because the analysis is executed asynchronously.
     */
    private Date lastAnalysisRequested;

    private GithubRepository githubRepository;

    private AnalysisInformation lastAnalysisInformation;

    // TODO move this to another class
    public void setStatus(AnalysisStatus status) {
        this.status = status;

        if (!status.equals(AnalysisStatus.FAILED)) {
            this.errorMessage = null;
        }

        if (!status.equals(AnalysisStatus.COMPLETED)) {
            if (this.getLastAnalysisInformation() != null) {
                this.getLastAnalysisInformation().setDeadCodeIssues(null);
            }
        }
    }

    /**
     * This is a hack to allow dynamically instantiate the POJO without using the builder.
     */
    @JsonPOJOBuilder(withPrefix = "")
    public static final class RepositoryBuilder {
    }
}
