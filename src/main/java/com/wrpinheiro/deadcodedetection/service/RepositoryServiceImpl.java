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

import static com.wrpinheiro.deadcodedetection.model.Language.JAVA;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

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
        if (repositoryDAO.findByName(url).isPresent()) {
            throw new DuplicatedEntity(String.format("Repository with URL %s already exists. If you want to check the " +
                    "dead code int the available repository, execute the endpoint repository/{repository.id}/checkCode", url));
        }

        Repository repository = Repository.builder()
                .githubRository(GithubRepository.builder().url(url).language(language).build())
//                .name(name)
//                .language(defaultIfNull(language, JAVA))
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
    public Repository findById(Long id) {
        return repositoryDAO.findById(id);
    }
}
