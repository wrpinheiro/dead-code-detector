package com.aurea.deadcodedetection.dao;

import com.aurea.deadcodedetection.model.Repository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.*;

/**
 * Created by wrpinheiro on 3/24/17.
 */
@Component
public class RepositoryDAOImpl implements RepositoryDAO {
    private AtomicLong repositoryIdSequence = new AtomicLong();

    private Map<Long, Repository> repositories = new ConcurrentHashMap();

    @Override
    public Repository save(Repository repository) {
        repository.setId(repositoryIdSequence.incrementAndGet());
        repositories.put(repository.getId(), repository);
        return repository;
    }

    @Override
    public List<Repository> findAll() {
        return repositories.values().stream().sorted(comparing(Repository::getRepositoryUrl))
                .collect(toList());
    }

    @Override
    public Repository findById(Long repositoryId) {
        return repositories.get(repositoryId);
    }
}
