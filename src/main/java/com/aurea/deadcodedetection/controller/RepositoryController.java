package com.aurea.deadcodedetection.controller;

import com.aurea.deadcodedetection.dto.AnalysedRepositoryDTO;
import com.aurea.deadcodedetection.model.Repository;
import com.aurea.deadcodedetection.repository.RepositoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

import static com.aurea.deadcodedetection.model.AnalysisStatus.ADDED;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Created by wrpinheiro on 3/21/17.
 */
@RestController
@RequestMapping("/repository")
public class RepositoryController {

    @Autowired
    private RepositoryRepository repository;

    @RequestMapping(name = "/", method = GET)
    public Iterable<Repository> getRepositories() {
        return repository.findAll();
    }

    @RequestMapping(name = "/", method = POST)
    public Repository addRepository(@Param("url") String url, @Param("name") String name) {
        Repository repository = new Repository();
        repository.setRepositoryUrl(url);
        repository.setRepositoryName(name);
        repository.setStatus(ADDED);
        repository.setCreatedAt(new Date());

        Repository newRepository = this.repository.save(repository);

        return newRepository;
    }

    @RequestMapping(path = "/{repositoryId}/deadcode", method = GET)
    public AnalysedRepositoryDTO getDeadcode(@PathVariable("repositoryId") Long repositoryId) {
        Repository repository = this.repository.findOne(repositoryId);

        return AnalysedRepositoryDTO.builder().repository(repository).build();
    }
}
