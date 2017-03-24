package com.aurea.deadcodedetection.service;

import com.aurea.deadcodedetection.dao.RepositoryDAO;
import com.aurea.deadcodedetection.model.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.aurea.deadcodedetection.model.AnalysisStatus.ADDED;

/**
 * Created by wrpinheiro on 3/24/17.
 */
@Service
public class RepositoryServiceImpl implements RepositoryService {
    @Autowired
    private RepositoryDAO repositoryDAO;

    private String getUniqueId() {
        return UUID.randomUUID().toString();
    }

    public Repository addRepository(String name, String url) {
        Repository repository = Repository.builder()
                .repositoryName(name)
                .repositoryUrl(url)
                .status(ADDED)
                .createdAt(new Date())
                .build();

        Repository newRepository = this.repositoryDAO.save(repository);

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