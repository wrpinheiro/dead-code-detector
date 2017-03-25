package com.aurea.deadcodedetection.service;

import com.aurea.deadcodedetection.model.Repository;

import java.util.List;

/**
 * Created by wrpinheiro on 3/23/17.
 */
public interface RepositoryService {
    Repository addRepository(String url);

    List<Repository> findAll();

    Repository findById(Long id);
}
