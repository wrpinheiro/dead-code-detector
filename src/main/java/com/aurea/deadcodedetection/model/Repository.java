package com.aurea.deadcodedetection.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by wrpinheiro on 3/21/17.
 */
@Data
@Entity
@Table(name = "repository")
public class Repository {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private AnalysisStatus status;

    /**
     * The time the repository was added
     */
    @NotNull
    @Column(name = "created_at")
    private Date createdAt;

    /**
     * The time the repository was processed
     */
    @Column(name = "processed_at")
    private Date processedAt;

    @NotNull
    @Column(name = "repository_url")
    private String repositoryUrl;

    @NotNull
    @Column(name = "repository_name")
    private String repositoryName;

    @Column(name = "repository_description")
    private String repositoryDescription;

}
