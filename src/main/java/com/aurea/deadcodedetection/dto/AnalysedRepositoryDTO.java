package com.aurea.deadcodedetection.dto;

import com.aurea.deadcodedetection.model.AnalysedFile;
import com.aurea.deadcodedetection.model.Repository;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Created by wrpinheiro on 3/21/17.
 */
@Data
@Builder
public class AnalysedRepositoryDTO {
    private Repository repository;

    private List<AnalysedFile> analysedFiles;
}
