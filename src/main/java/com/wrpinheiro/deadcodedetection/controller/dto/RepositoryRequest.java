package com.wrpinheiro.deadcodedetection.controller.dto;

import com.wrpinheiro.deadcodedetection.model.Language;
import com.wrpinheiro.deadcodedetection.validation.GithubUrl;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * The request model used to create a new repository.
 *
 * @author wrpinheiro
 */
@Data
public class RepositoryRequest {
    @ApiModelProperty(required = true, allowableValues = "https://github.com/<owner>/<repository>.git")
    @GithubUrl
    private String url;

    @ApiModelProperty(value = "The language used to analyze the repository", required = false,
            allowableValues = "JAVA, ADA, CPP, FORTRAN")
    private Language language;

    @ApiModelProperty(value = "The branch that will be used to clone the repository", required = false)
    private String branch;
}
