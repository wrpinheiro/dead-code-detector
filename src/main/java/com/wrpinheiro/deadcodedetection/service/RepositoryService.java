package com.wrpinheiro.deadcodedetection.service;

import com.wrpinheiro.deadcodedetection.model.Language;
import com.wrpinheiro.deadcodedetection.model.Repository;

import java.util.List;

/**
 * @author wrpinheiro
 */
public interface RepositoryService {
    Repository addRepository(String url, String branch, Language language);

    List<Repository> findAll();

    void analyze(Repository newRepository);

    void removeRepository(Repository repository);

    Repository findByUUID(String repositoryUUID);
}
