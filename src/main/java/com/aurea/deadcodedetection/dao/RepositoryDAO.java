package com.aurea.deadcodedetection.dao;

import com.aurea.deadcodedetection.model.Repository;

import java.util.List;

/**
 * Created by wrpinheiro on 3/24/17.
 */
public interface RepositoryDAO {
    Repository save(Repository repository);

    List<Repository> findAll();

    Repository findById(Long repositoryId);
}
