package com.aurea.deadcodedetection.model;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by wrpinheiro on 3/21/17.
 */
@Data
@Builder
public class Repository {
    private Long id;

    @NotNull
    private AnalysisStatus status;

    /**
     * The time the repository was added
     */
    @NotNull
    private Date createdAt;

    /**
     * The time the repository was processed
     */
    private Date processedAt;

    @NotNull
    private String repositoryUrl;

    @NotNull
    private String repositoryName;

    private String repositoryDescription;
}
