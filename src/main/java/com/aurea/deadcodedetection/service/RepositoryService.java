package com.aurea.deadcodedetection.service;

import com.aurea.deadcodedetection.dto.AnalysedRepositoryDTO;
import com.aurea.deadcodedetection.model.Repository;
import com.aurea.deadcodedetection.repository.RepositoryRepository;
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
@Produces(MediaType.APPLICATION_JSON)
public class RepositoryService {
    @Autowired
    private RepositoryRepository repository;

    @GET
    public Iterable<Repository> getRepositories() {
        return repository.findAll();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Repository addRepository(Map<String, String> params) {
        Repository repository = new Repository();
        repository.setRepositoryUrl(params.get("url"));
        repository.setRepositoryName(params.get("name"));
        repository.setStatus(ADDED);
        repository.setCreatedAt(new Date());

        Repository newRepository = this.repository.save(repository);

        return newRepository;
    }

    @GET
    @Path("/{repositoryId}/deadcode")
    public AnalysedRepositoryDTO getDeadcode(@PathParam("repositoryId") Long repositoryId) {
        Repository repository = this.repository.findOne(repositoryId);

        return new AnalysedRepositoryDTO(repository);
    }
}
