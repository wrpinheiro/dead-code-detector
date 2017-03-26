package com.wrpinheiro.deadcodedetection.dao;

import com.wrpinheiro.deadcodedetection.model.Repository;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

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
        return repositories.values().stream().sorted(
                Comparator.comparing(repo -> repo.getGithubRository().getUrl()))
                .collect(toList());
    }

    @Override
    public Repository findById(Long repositoryId) {
        return repositories.get(repositoryId);
    }

    @Override
    public Optional<Repository> findByName(String name) {
        return repositories.values()
                .stream().filter(repo -> repo.getGithubRository().equals(name)).findFirst();
    }

//    @Override
//    public Optional<Repository> findByUrl(String url) {
//        return repositories.values()
//                .stream().filter(repo -> repo.getUrl().equals(url)).findFirst();
//    }

    @Override
    public void remove(Long id) {
        this.repositories.remove(id);
    }
}
