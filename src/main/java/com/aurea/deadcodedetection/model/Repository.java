package com.aurea.deadcodedetection.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by wrpinheiro on 3/21/17.
 */
@Data
@Builder
public class Repository {
    private Long id;

    @NotNull
    private AnalysisStatus status;

    private String errorMessage;

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

    private List<CodeSmell> codeSmells;
}
