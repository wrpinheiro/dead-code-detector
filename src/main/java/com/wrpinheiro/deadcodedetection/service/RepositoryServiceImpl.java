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

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

/**
 * Created by wrpinheiro on 3/24/17.
 */
@Service
public class RepositoryServiceImpl implements RepositoryService {
    @Autowired
    private RepositoryDAO repositoryDAO;

    @Autowired
    private AnalysisService analysisService;

    private final static String DEFAULT_BRANCH = "master";
    private static final Language DEFAULT_LANGUAGE = Language.JAVA;

    public Repository addRepository(String name, String url, String branch, Language language) {
        if (repositoryDAO.findByName(name).isPresent()) {
            throw new DuplicatedEntity(String.format("Repository with name %s already exists. If you want to analyze this" +
                    "repository execute the endpoint repository/{repository.name}/analyze", url));
        }

        Repository repository = Repository.builder()
                .githubRepository(
                        GithubRepository
                                .builder()
                                .url(url)
                                .language(defaultIfNull(language, DEFAULT_LANGUAGE))
                                .branch(defaultIfBlank(branch, DEFAULT_BRANCH))
                                .build())
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
