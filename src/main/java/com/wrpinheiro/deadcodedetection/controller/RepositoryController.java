package com.wrpinheiro.deadcodedetection.controller;

import com.wrpinheiro.deadcodedetection.controller.dto.RepositoryRequest;
import com.wrpinheiro.deadcodedetection.controller.dto.SimpleRepositoryResponse;
import com.wrpinheiro.deadcodedetection.exceptions.DuplicatedEntity;
import com.wrpinheiro.deadcodedetection.exceptions.InvalidStateException;
import com.wrpinheiro.deadcodedetection.model.Repository;
import com.wrpinheiro.deadcodedetection.service.RepositoryService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by wrpinheiro on 3/22/17.
 */
@Component
@Path("/repository")
@Produces("application/json")
@Consumes("application/json")
@Api(value = "Repository controller", produces = "application/json")
public class RepositoryController {
    @Autowired
    private RepositoryService repositoryService;

    @ApiOperation(value = "List all repositories analyzed. This is a simplified view of repository without the code " +
            "smells.", response = SimpleRepositoryResponse.class, responseContainer = "List")
    @ApiResponses(value = @ApiResponse(code = 200, message = "Zero or more repositories found",
            responseContainer = "List", response = Repository.class))
    @GET
    public List<SimpleRepositoryResponse> getRepositories() {
        return repositoryService.findAll().stream().map(repository -> new SimpleRepositoryResponse(repository))
                .collect(Collectors.toList());
    }

    @ApiOperation(value = "Create a repository.", response = Repository.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The repository created"),
            @ApiResponse(code = 409, message = "A repository with the same NAME already exists. Please select a new " +
                    "name and POST again. If you want to check the dead code execute a POST to the endpoint " +
                    "repository/{repository.name}/analyze"),
            @ApiResponse(code = 412, message = "NAME and URL are required fields")
    })
    @POST
    public Repository addRepository(@ApiParam(value = "Repository to be added and analyzed. The supported languages are:" +
            "JAVA, ADA, CPP and FORTRAN") RepositoryRequest repositoryRequest) {

        try {
            if (repositoryRequest.getName() == null || repositoryRequest.getUrl() == null) {
                throw new WebApplicationException(Response.Status.PRECONDITION_FAILED);
            }

            return repositoryService.addRepository(repositoryRequest.getName(), repositoryRequest.getUrl(),
                    repositoryRequest.getBranch(), repositoryRequest.getLanguage());
        } catch(DuplicatedEntity ex) {
            throw new WebApplicationException(ex, Response.Status.CONFLICT);
        }
    }

    @ApiOperation(value = "List the dead code issues found in the repository.", response = Repository.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The repository created", response = Repository.class),
            @ApiResponse(code = 404, message = "Repository with the requested name could not found"),})
    @GET
    @Path("/{repositoryName}")
    public Repository getRepositoryIssues(@ApiParam(name = "repositoryName", value = "the repository name to search")
                                                 @PathParam("repositoryName") String repositoryName) {
        Repository repository = repositoryService.findByName(repositoryName);

        if (repository == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        return repository;
    }

    @POST
    @ApiOperation(value = "Analyze a repository to find dead code.", response = SimpleRepositoryResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The repository is going to be analyzed (search for dead code"),
            @ApiResponse(code = 404, message = "A repository with the requested name could not be found"),
            @ApiResponse(code = 412, message = "Can't analyze a repository already being analyzed")
    })
    @Path("{repositoryName}/analyze")
    public Repository analyzeRepository(@ApiParam(name = "repositoryName", value = "The repository name to be analyzed")
                                            @PathParam("repositoryName") String repositoryName) {
        try {
            Repository repository = repositoryService.findByName(repositoryName);

            if (repository == null) {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }

            repositoryService.analyze(repository);

            return repository;
        } catch (InvalidStateException ex) {
            throw new WebApplicationException(Response.Status.PRECONDITION_FAILED);
        }
    }

    @DELETE
    @ApiOperation(value = "Remove a previously added repository", response = Repository.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The repository was deleted"),
            @ApiResponse(code = 404, message = "The repository could not be found"),
            @ApiResponse(code = 412, message = "Trying to remove a repository while being analyzed"),
    })
    @Path("{repositoryName}")
    public Repository removeRepository(@ApiParam(name = "repositoryName", value = "The repository name to find dead " +
            "code issues") @PathParam("repositoryName") String repositoryName) {
        try {
            Repository repository = repositoryService.findByName(repositoryName);

            if (repository == null) {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }

            repositoryService.removeRepository(repository);

            return repository;
        } catch(InvalidStateException ex) {
            throw new WebApplicationException(Response.Status.PRECONDITION_FAILED);
        }
    }
}
