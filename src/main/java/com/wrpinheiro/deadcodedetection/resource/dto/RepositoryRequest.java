package com.wrpinheiro.deadcodedetection.resource.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by wrpinheiro on 3/22/17.
 */
@Data
public class RepositoryRequest {
    @ApiModelProperty(value = "A Github url",
            allowableValues = "a url in the format: https://github.com/<ownser>/<repository>.git")
    private String url;
}
