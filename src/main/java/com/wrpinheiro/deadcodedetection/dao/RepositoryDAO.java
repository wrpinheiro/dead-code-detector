package com.wrpinheiro.deadcodedetection.dao;

import com.wrpinheiro.deadcodedetection.model.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Created by wrpinheiro on 3/24/17.
 */
public interface RepositoryDAO {
    Repository save(Repository repository);

    List<Repository> findAll();

    Repository findById(Long repositoryId);

//    Optional<Repository> findByUrl(String url);

    Optional<Repository> findByName(String name);

    void remove(Long id);
}
