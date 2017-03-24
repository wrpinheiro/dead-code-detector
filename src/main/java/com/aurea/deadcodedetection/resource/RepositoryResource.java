package com.aurea.deadcodedetection.resource;

import com.aurea.deadcodedetection.dto.AnalysedRepositoryDTO;
import com.aurea.deadcodedetection.model.Repository;
import com.aurea.deadcodedetection.service.RepositoryService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;

/**
 * Created by wrpinheiro on 3/22/17.
 */
@Component
@Path("/repository")
@Produces("application/vnd.dcd.v1+json")
@Consumes("application/vnd.dcd.v1+json")
@Api(value = "Repository resource", produces = "application/vnd.dcd.v1+json")
public class RepositoryResource {
    @Autowired
    private RepositoryService repositoryService;

    @ApiOperation(value = "List all repositories analyzed. Version 1 - (version in header: application/vnd.dcd.v1+json)", response = Repository.class, responseContainer = "Iterable")
    @ApiResponses(value = @ApiResponse(code = 200, message = "Zero or more repositories found"))
    @GET
    public Iterable<Repository> getRepositories() {
        return repositoryService.findAll();
    }

    @ApiOperation(value = "Create a repository. Version 1 - (version in header: application/vnd.dcd.v1+json)", response = Repository.class)
    @ApiResponses(value = @ApiResponse(code = 200, message = "The repository created"))
    @POST
    public Repository addRepository(@ApiParam(value = "Repository to be added and analyzed")
                                            RepositoryRequest repositoryRequest) {

        return repositoryService.addRepository(repositoryRequest.getName(), repositoryRequest.getUrl());
    }

    @ApiOperation(value = "List the dead code found in the repository. Version 1 - (version in header: application/vnd.dcd.v1+json)", response = AnalysedRepositoryDTO.class)
    @ApiResponses(value = @ApiResponse(code = 200, message = "The repository created"))
    @GET
    @Path("/{repositoryId}")
    public AnalysedRepositoryDTO getRepositoryCodeSmellsAnalysis(@ApiParam(name = "repositoryId", value = "the repository Id to search")
                                                 @PathParam("repositoryId") Long repositoryId) {
        Repository repository = this.repositoryService.findById(repositoryId);

        return new AnalysedRepositoryDTO(repository);
    }
}
