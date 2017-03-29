package com.wrpinheiro.deadcodedetection.controller;

import com.wrpinheiro.deadcodedetection.controller.dto.RepositoryRequest;
import com.wrpinheiro.deadcodedetection.controller.dto.SimpleRepositoryResponse;
import com.wrpinheiro.deadcodedetection.exceptions.DuplicatedEntity;
import com.wrpinheiro.deadcodedetection.exceptions.InvalidStateException;
import com.wrpinheiro.deadcodedetection.exceptions.PaginationException;
import com.wrpinheiro.deadcodedetection.model.Paginator;
import com.wrpinheiro.deadcodedetection.model.Repository;
import com.wrpinheiro.deadcodedetection.service.PaginationService;
import com.wrpinheiro.deadcodedetection.service.RepositoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 * The repository controller.
 *
 * @author wrpinheiro
 */
@Component
@Path("/repository")
@Produces("application/json")
@Consumes("application/json")
@Api(value = "Repository controller", produces = "application/json")
public class RepositoryController {
    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private PaginationService paginationService;

    @ApiOperation(value = "List all repositories analyzed, sorted by the Github repository URL. This is a simplified "
            + "view of the repository without the code issues.", response = SimpleRepositoryResponse.class,
            responseContainer = "Paginator")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Zero or more repositories found",  responseContainer = "List",
                    response = Repository.class),
            @ApiResponse(code = 412, message = "Parameters page and pageSize must be equal or greater than 1")
    })
    @GET
    public Paginator<SimpleRepositoryResponse> getRepositories(
            @ApiParam(value="the page to be shown") @DefaultValue("1") @QueryParam("page") Integer page,
            @ApiParam(value="the max number of elements per page") @DefaultValue("10") @QueryParam("pageSize")
                    Integer pageSize) {
        try {
            List<SimpleRepositoryResponse> response = repositoryService
                    .findAll()
                    .stream()
                    .map(repository -> new SimpleRepositoryResponse(repository))
                    .collect(Collectors.toList());

            return paginationService.getPage(response, page, pageSize);
        } catch (PaginationException ex) {
            throw new WebApplicationException(ex, Response.Status.PRECONDITION_FAILED);
        }
    }

    /**
     * API operation to add a repository.
     */
    @ApiOperation(value = "Add a repository.", response = Repository.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The repository added"),
            @ApiResponse(code = 409, message = "A repository with the same URL and BRANCH already exists. Please "
                    + "select different values and POST again. If you want to check the dead code execute a POST to "
                    + "the endpoint repository/{repository.uuid}/analyze"),
            @ApiResponse(code = 412, message = "URL is a required field")
    })
    @POST
    public Repository addRepository(@ApiParam(value = "Repository to be added and analyzed. The supported languages "
            + "are: JAVA, ADA, CPP and FORTRAN") final RepositoryRequest repositoryRequest) {

        try {
            if (repositoryRequest.getUrl() == null) {
                throw new WebApplicationException(Response.Status.PRECONDITION_FAILED);
            }

            return repositoryService.addRepository(repositoryRequest.getUrl(), repositoryRequest.getBranch(),
                    repositoryRequest.getLanguage());
        } catch (DuplicatedEntity ex) {
            throw new WebApplicationException(ex, Response.Status.CONFLICT);
        }
    }

    /**
     * API operation to get the information of a repository.
     */
    @ApiOperation(value = "List the dead code issues found in the repository.", response = Repository.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The repository created", response = Repository.class),
            @ApiResponse(code = 404, message = "Repository with the requested uuid could not found"),})
    @GET
    @Path("/{repositoryUUID}")
    public Repository getRepositoryIssues(@ApiParam(name = "repositoryUUID", value = "the repository uuid to search")
                                          @PathParam("repositoryUUID") final String repositoryUUID) {
        final Repository repository = repositoryService.findByUUID(repositoryUUID);

        if (repository == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        return repository;
    }

    /**
     * API operation to analyze one repository.
     */
    @POST
    @ApiOperation(value = "Analyze a repository to find dead code.", response = SimpleRepositoryResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The repository is going to be analyzed (search for dead code"),
            @ApiResponse(code = 404, message = "A repository with the requested UUID could not be found"),
            @ApiResponse(code = 412, message = "Can't analyze a repository already being analyzed")
    })
    @Path("{repositoryUUID}/analyze")
    public Repository analyzeRepository(@ApiParam(name = "repositoryUUID", value = "The repository UUID to be analyzed")
                                        @PathParam("repositoryUUID") final String repositoryUUID) {
        try {
            final Repository repository = repositoryService.findByUUID(repositoryUUID);

            if (repository == null) {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }

            repositoryService.analyze(repository);

            return repository;
        } catch (InvalidStateException ex) {
            throw new WebApplicationException(Response.Status.PRECONDITION_FAILED);
        }
    }

    /**
     * API operation to remove a repository.
     */
    @DELETE
    @ApiOperation(value = "Remove a previously added repository and its analysis", response = Repository.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The repository was deleted"),
            @ApiResponse(code = 404, message = "The repository could not be found"),
            @ApiResponse(code = 412, message = "Trying to remove a repository while being analyzed"),
    })
    @Path("{repositoryUUID}")
    public Repository removeRepository(@ApiParam(name = "repositoryUUID", value = "The repository UUID to remove")
                                       @PathParam("repositoryUUID") final String repositoryUUID) {
        try {
            final Repository repository = repositoryService.findByUUID(repositoryUUID);

            if (repository == null) {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }

            repositoryService.removeRepository(repository);

            return repository;
        } catch (InvalidStateException ex) {
            throw new WebApplicationException(Response.Status.PRECONDITION_FAILED);
        }
    }
}
