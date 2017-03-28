package com.wrpinheiro.deadcodedetection.dao;

import com.wrpinheiro.deadcodedetection.model.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author wrpinheiro
 */
public interface RepositoryDAO {
    Repository save(Repository repository);

    List<Repository> findAll();

    Optional<Repository> findByUrlAndBranch(String url, String branch);

    void remove(String uuid);

    Repository findByUUID(String repositoryUUID);
}
