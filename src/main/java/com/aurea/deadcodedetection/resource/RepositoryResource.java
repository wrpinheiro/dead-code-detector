package com.aurea.deadcodedetection.resource;

import com.aurea.deadcodedetection.model.Repository;
import com.aurea.deadcodedetection.resource.dto.RepositoryRequest;
import com.aurea.deadcodedetection.resource.dto.SimpleRepositoryResponse;
import com.aurea.deadcodedetection.service.RepositoryService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by wrpinheiro on 3/22/17.
 */
@Component
@Path("/repository")
@Produces("application/vnd.dcd.v1+json")
@Consumes("application/vnd.dcd.v1+json")
@Api(value = "Repository resource (v1 - version in header)", produces = "application/vnd.dcd.v1+json")
public class RepositoryResource {
    @Autowired
    private RepositoryService repositoryService;

    @ApiOperation(value = "List all repositories analyzed. This is a simplified view of repository without the code smells.",
            response = Repository.class, responseContainer = "List")
    @ApiResponses(value = @ApiResponse(code = 200, message = "Zero or more repositories found"))
    @GET
    public List<SimpleRepositoryResponse> getRepositories() {
        return repositoryService.findAll().stream().map(repository -> new SimpleRepositoryResponse(repository))
                .collect(Collectors.toList());
    }

    @ApiOperation(value = "Create a repository.", response = Repository.class)
    @ApiResponses(value = @ApiResponse(code = 200, message = "The repository created"))
    @POST
    public Repository addRepository(@ApiParam(value = "Repository to be added and analyzed")
                                            RepositoryRequest repositoryRequest) {

        return repositoryService.addRepository(repositoryRequest.getName(), repositoryRequest.getUrl());
    }

    @ApiOperation(value = "List the dead code found in the repository.", response = SimpleRepositoryResponse.class)
    @ApiResponses(value = @ApiResponse(code = 200, message = "The repository created"))
    @GET
    @Path("/{repositoryId}")
    public Repository getRepositoryCodeSmellsAnalysis(@ApiParam(name = "repositoryId", value = "the repository Id to search")
                                                 @PathParam("repositoryId") Long repositoryId) {
        return this.repositoryService.findById(repositoryId);
    }
}
