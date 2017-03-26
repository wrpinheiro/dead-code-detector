package com.wrpinheiro.deadcodedetection.service;

import com.wrpinheiro.deadcodedetection.dao.RepositoryDAO;
import com.wrpinheiro.deadcodedetection.exceptions.DuplicatedEntity;
import com.wrpinheiro.deadcodedetection.exceptions.InvalidStateException;
import com.wrpinheiro.deadcodedetection.model.AnalysisStatus;
import com.wrpinheiro.deadcodedetection.model.GithubRepository;
import com.wrpinheiro.deadcodedetection.model.Language;
import com.wrpinheiro.deadcodedetection.model.Repository;
import com.wrpinheiro.deadcodedetection.service.analysis.AnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by wrpinheiro on 3/24/17.
 */
@Service
public class RepositoryServiceImpl implements RepositoryService {
    @Autowired
    private RepositoryDAO repositoryDAO;

    @Autowired
    private AnalysisService analysisService;

    public Repository addRepository(String name, String url, Language language) {
        if (repositoryDAO.findByName(name).isPresent()) {
            throw new DuplicatedEntity(String.format("Repository with name %s already exists. If you want to analyze this" +
                    "repository execute the endpoint repository/{repository.name}/analyze", url));
        }

        Repository repository = Repository.builder()
                .githubRepository(GithubRepository.builder().url(url).language(language).build())
                .name(name)
                .status(AnalysisStatus.ADDED)
                .createdAt(new Date())
                .build();

        Repository newRepository = this.repositoryDAO.save(repository);

        analyze(newRepository);

        return newRepository;
    }

    public void analyze(Repository newRepository) {
        if (newRepository.getStatus().equals(AnalysisStatus.PROCESSING)) {
            throw new InvalidStateException("Can't analyze a repository already being analyzed");
        }
        analysisService.analyze(newRepository);
    }

    @Override
    public void removeRepository(Repository repository) {
        if (repository.getStatus().equals(AnalysisStatus.PROCESSING)) {
            throw new InvalidStateException("Could not remove a repository while being analyzed");
        }
        repositoryDAO.remove(repository.getId());
    }

    public List<Repository> findAll() {
        return repositoryDAO.findAll();
    }

    @Override
    public Repository findByName(String repositoryName) {
        return repositoryDAO.findByName(repositoryName).orElse(null);
    }

//    @Override
//    public Repository findById(Long id) {
//        return repositoryDAO.findById(id);
//    }
}
