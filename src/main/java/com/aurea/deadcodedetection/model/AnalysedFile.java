package com.aurea.deadcodedetection.model;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 * Created by wrpinheiro on 3/21/17.
 */
@Data
@Entity
@Table(name = "analysed_file")
public class AnalysedFile {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "repository_id", nullable = false)
    private Repository repository;

    @Column(name = "filename")
    private String filename;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "filename")
    private List<DeadCode> deadCode;
}
