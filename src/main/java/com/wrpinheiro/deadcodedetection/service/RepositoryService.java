package com.wrpinheiro.deadcodedetection.service;

import com.wrpinheiro.deadcodedetection.model.Language;
import com.wrpinheiro.deadcodedetection.model.Repository;

import java.util.List;

/**
 * Created by wrpinheiro on 3/23/17.
 */
public interface RepositoryService {
    Repository addRepository(String url, String branch, Language language);

    List<Repository> findAll();

    void analyze(Repository newRepository);

    void removeRepository(Repository repository);

    Repository findByUUID(String repositoryUUID);
}
