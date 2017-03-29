package com.wrpinheiro.deadcodedetection.dao;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

import com.wrpinheiro.deadcodedetection.model.GithubRepository;
import com.wrpinheiro.deadcodedetection.model.Repository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * DAO implementation for Repository entity.
 *
 * @author wrpinheiro
 */
@Component
public class RepositoryDAOImpl implements RepositoryDAO {
    private Map<String, Repository> repositories = new ConcurrentHashMap<>();

    @Override
    public Repository save(Repository repository) {
        repositories.put(repository.getUuid(), repository);
        return repository;
    }

    @Override
    public List<Repository> findAll() {
        return repositories.values().stream().sorted(
                comparing(repo -> repo.getGithubRepository().getUrl()))
                .collect(toList());
    }

    @Override
    public Optional<Repository> findByUrlAndBranch(String url, String branch) {
        return repositories.values()
                .stream().filter(repo -> {
                    GithubRepository githubRepository = repo.getGithubRepository();
                    return githubRepository.getUrl().equals(url) && githubRepository.getBranch().equals(branch);
                }).findFirst();
    }

    @Override
    public void remove(String uuid) {
        this.repositories.remove(uuid);
    }

    @Override
    public Repository findByUUID(String uuid) {
        return this.repositories.get(uuid);
    }
}
