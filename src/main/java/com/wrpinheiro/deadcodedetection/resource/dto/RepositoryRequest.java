package com.wrpinheiro.deadcodedetection.resource.dto;

import com.wrpinheiro.deadcodedetection.model.Language;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by wrpinheiro on 3/22/17.
 */
@Data
public class RepositoryRequest {
    @ApiModelProperty(value = "A name for this repository. Must be unique!", required = true)
    private String name;

    @ApiModelProperty(required = true, allowableValues = "https://github.com/<owner>/<repository>.git")
    private String url;

    @ApiModelProperty(value = "The language used to analyze the repository", required = false,
            allowableValues = "JAVA, ADA, CPP, FORTRAN")
    private Language language;

    @ApiModelProperty(value = "The branch that will be used to clone the repository", required = false)
    private String branch;
}
