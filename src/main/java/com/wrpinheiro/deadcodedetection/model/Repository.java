package com.wrpinheiro.deadcodedetection.model;

import static com.wrpinheiro.deadcodedetection.model.RepositoryStatus.COMPLETED;
import static com.wrpinheiro.deadcodedetection.model.RepositoryStatus.FAILED;
import static com.wrpinheiro.deadcodedetection.model.RepositoryStatus.PROCESSING;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * A model representing a repository.
 *
 * @author wrpinheiro
 */
@JsonDeserialize(builder = Repository.RepositoryBuilder.class)
@Data
@Builder
public class Repository {
    private String uuid;

    private RepositoryStatus status;

    private Date createdAt;

    private Date lastAnalysisRequested;

    private GithubRepository githubRepository;

    private AnalysisInformation lastAnalysisInformation;

    /**
     * Set the status of the repository. Guarantee to clear dependent attributes.
     *
     * @param status the new status.
     */
    public void setStatus(final RepositoryStatus status) {
        this.status = status;

        if (this.getLastAnalysisInformation() != null) {
            if (!status.equals(FAILED)) {
                this.getLastAnalysisInformation().setErrorMessage(null);
            }

            if (!status.equals(COMPLETED)) {
                this.getLastAnalysisInformation().setDeadCodeIssues(null);
            }
        }
    }

    /**
     * Notified this repository that it's being analyzed.
     *
     * @param analysisInformation the analysis information for this repository
     */
    public void startingAnalysis(final AnalysisInformation analysisInformation) {
        setStatus(PROCESSING);
        setLastAnalysisInformation(analysisInformation);
    }

    public void finishAnalysis() {
        setStatus(COMPLETED);
    }

    /**
     * This is a hack to allow dynamically instantiate the POJO without using the builder.
     */
    @JsonPOJOBuilder(withPrefix = "")
    public static final class RepositoryBuilder {
    }
}
