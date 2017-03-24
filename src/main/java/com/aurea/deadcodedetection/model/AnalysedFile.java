package com.aurea.deadcodedetection.model;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 * Created by wrpinheiro on 3/21/17.
 */
@Data
public class AnalysedFile {
    private Long id;

    private Repository repository;

    private String filename;

    private List<DeadCode> deadCode;
}
