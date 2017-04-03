package com.wrpinheiro.deadcodedetection.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.wrpinheiro.deadcodedetection.dto.RepositoryRequest;
import com.wrpinheiro.deadcodedetection.model.Paginator;
import com.wrpinheiro.deadcodedetection.model.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * A client class to abstract the accesses to the repository endpoint.
 *
 * @author wrpinheiro
 */
@Component
public class RepositoryClient {
    @Autowired
    private TestRestTemplate restTemplate;

    /**
     * Return a page of the list of all repositories.
     */
    public ResponseEntity<Paginator> findAllRepositories() {
        final ResponseEntity<Paginator> entity  = restTemplate.getForEntity("/api/repository",  Paginator.class);

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        return entity;
    }

    /**
     * Executes a post to the repository endpoint and return a generic type.
     */
    public ResponseEntity<Map> postToRepository(final RepositoryRequest repositoryRequest) {
        return this.restTemplate.postForEntity("/api/repository", repositoryRequest, Map.class);
    }

    /**
     * Create a repository with a given github URL and a branch.
     *
     * @param url the github URL
     * @param branch the branch to be cloned
     */
    public ResponseEntity<Repository> createRepository(final String url, final String branch) {
        final RepositoryRequest request = new RepositoryRequest();
        request.setUrl(url);
        request.setBranch(branch);

        final ResponseEntity<Repository> entity = this.restTemplate.postForEntity("/api/repository",
                request, Repository.class);

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        return entity;
    }

    /**
     * Create a repository with a given github url and using the master branch.
     * @param url the github url
     */
    public ResponseEntity<Repository> createRepository(final String url) {
        return this.createRepository(url, null);
    }

    /**
     * Find a repository by a given UUID.
     * @param uuid the repository UUID
     */
    public ResponseEntity<Repository> findRepositoryByUUID(final String uuid) {
        final ResponseEntity<Repository> entity = this.restTemplate.getForEntity("/api/repository/"
                + uuid, Repository.class);

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);

        return entity;
    }
}
