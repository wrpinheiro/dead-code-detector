package com.aurea.deadcodedetection.resource;

import com.aurea.deadcodedetection.dto.AnalysedRepositoryDTO;
import com.aurea.deadcodedetection.model.Repository;
import com.aurea.deadcodedetection.repository.RepositoryRepository;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Date;
import java.util.Map;

import static com.aurea.deadcodedetection.model.AnalysisStatus.ADDED;

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
    private RepositoryRepository repository;

    @ApiOperation(value = "List all repositories analyzed. Version 1 - (version in header: application/vnd.dcd.v1+json)", response = Repository.class, responseContainer = "Iterable")
    @ApiResponses(value = @ApiResponse(code = 200, message = "Zero or more repositories found"))
    @GET
    public Iterable<Repository> getRepositories() {
        return repository.findAll();
    }

    @ApiOperation(value = "Create a repository. Version 1 - (version in header: application/vnd.dcd.v1+json)", response = Repository.class)
    @ApiResponses(value = @ApiResponse(code = 200, message = "The repository created"))
    @POST
    public Repository addRepository(@ApiParam(value = "Repository to be added and analyzed")
                                                RepositoryRequest repositoryRequest) {
        Repository repository = new Repository();
        repository.setRepositoryUrl(repositoryRequest.getUrl());
        repository.setRepositoryName(repositoryRequest.getName());
        repository.setStatus(ADDED);
        repository.setCreatedAt(new Date());

        Repository newRepository = this.repository.save(repository);

        return newRepository;
    }

    @ApiOperation(value = "List the dead code found in the repository. Version 1 - (version in header: application/vnd.dcd.v1+json)", response = AnalysedRepositoryDTO.class)
    @ApiResponses(value = @ApiResponse(code = 200, message = "The repository created"))
    @GET
    @Path("/{repositoryId}/deadcode")
    public AnalysedRepositoryDTO getDeadcode(@ApiParam(name = "repositoryId", value = "the repository Id to search")
                                                 @PathParam("repositoryId") Long repositoryId) {
        Repository repository = this.repository.findOne(repositoryId);

        return new AnalysedRepositoryDTO(repository);
    }
}
