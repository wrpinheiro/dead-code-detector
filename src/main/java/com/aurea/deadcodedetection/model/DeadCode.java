package com.aurea.deadcodedetection.model;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Created by wrpinheiro on 3/21/17.
 */
@Data
public class DeadCode {
    private Long id;

    @NotNull
    private DeadCodeType deadCodeType;

    @NotNull
    private AnalysedFile filename;

    @NotNull
    private Integer lineNumer;
}
