package com.aurea.assignment.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

/**
 * Created by wrpinheiro on 3/21/17.
 */
@Data
@AllArgsConstructor
public class Repository {
    private Long id;

    private AnalysisStatus status;

    /**
     * The time the repository was added
     */
    private Date createAt;

    /**
     * The time the repository was processed
     */
    private Date processedAt;

    private String repositoryUrl;

    private String repositoryName;
}


