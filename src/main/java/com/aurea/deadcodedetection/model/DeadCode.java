package com.aurea.deadcodedetection.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by wrpinheiro on 3/21/17.
 */
@Data
@Entity
@Table(name = "dead_code")
public class DeadCode {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    @NotNull
    private DeadCodeType deadCodeType;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "analysed_file_id", nullable = false)
    private AnalysedFile filename;

    @NotNull
    @Column(name = "line_number")
    private Integer lineNumer;
}
