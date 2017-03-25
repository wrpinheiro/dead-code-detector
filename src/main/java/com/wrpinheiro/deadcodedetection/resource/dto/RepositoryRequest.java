package com.wrpinheiro.deadcodedetection.resource.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by wrpinheiro on 3/22/17.
 */
@Data
public class RepositoryRequest {
    @ApiModelProperty(allowableValues = "A Github url in format: https://github.com/<owner>/<repository>.git")
    private String url;
}
