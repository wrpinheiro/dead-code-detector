package com.wrpinheiro.deadcodedetection.service;

import com.wrpinheiro.deadcodedetection.dao.RepositoryDAO;
import com.wrpinheiro.deadcodedetection.model.Repository;
import com.wrpinheiro.deadcodedetection.service.analysis.AnalysisService;
import com.wrpinheiro.deadcodedetection.model.AnalysisStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by wrpinheiro on 3/24/17.
 */
@Service
public class RepositoryServiceImpl implements RepositoryService {
    @Autowired
    private RepositoryDAO repositoryDAO;

    @Autowired
    private AnalysisService analysisService;

    private String getUniqueId() {
        return UUID.randomUUID().toString();
    }

    public Repository addRepository(String url) {
        Repository repository = Repository.builder()
                .url(url)
                .status(AnalysisStatus.ADDED)
                .createdAt(new Date())
                .build();

        Repository newRepository = this.repositoryDAO.save(repository);

        analysisService.analyse(newRepository);

        return newRepository;
    }

    public List<Repository> findAll() {
        return repositoryDAO.findAll();
    }

    @Override
    public Repository findById(Long id) {
        return repositoryDAO.findById(id);
    }
}
