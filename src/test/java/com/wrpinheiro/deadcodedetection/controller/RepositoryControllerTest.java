package com.wrpinheiro.deadcodedetection.controller;

import com.wrpinheiro.deadcodedetection.controller.dto.RepositoryRequest;
import com.wrpinheiro.deadcodedetection.model.Language;
import com.wrpinheiro.deadcodedetection.model.Repository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by wrpinheiro on 3/26/17.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RepositoryControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;

    // GET /api/repository
    @Test
    public void mustReturnAListOfRepositories() {
        ResponseEntity<Repository[]> entity = this.restTemplate.getForEntity("/api/repository", Repository[].class);
        assertThat(entity.getBody()).isNotNull();
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    // POST /api/repository
    @Test
    public void mustReturnErrorDueToMissingNameAndUrl() {
        RepositoryRequest request = new RepositoryRequest();
        ResponseEntity<Map> entity = this.restTemplate.postForEntity("/api/repository",
                request, Map.class);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.PRECONDITION_FAILED);
    }

    // POST /api/repository
    @Test
    public void mustCreateARepositoryWithBranchAndDefaultLanguage() {
        RepositoryRequest request = new RepositoryRequest();
        request.setUrl("https://github.com/wrpinheiro/jgraphlib.git");

        ResponseEntity<Repository> entity = this.restTemplate.postForEntity("/api/repository",
                request, Repository.class);

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(entity.getBody().getName()).isEqualTo("jgraphlib");
        assertThat(entity.getBody().getGithubRepository().getLanguage()).isEqualTo(Language.JAVA);
        assertThat(entity.getBody().getGithubRepository().getBranch()).isEqualTo("master");
    }

    // POST /api/repository/{uuid}
    @Test
    public void mustReturnRepositoryJustCreated() {
        RepositoryRequest request = new RepositoryRequest();
        request.setUrl("https://github.com/wrpinheiro/jgraphlib.git");

        ResponseEntity<Repository> entityCreated = this.restTemplate.postForEntity("/api/repository",
                request, Repository.class);

        assertThat(entityCreated.getStatusCode()).isEqualTo(HttpStatus.OK);
        Repository repoCreated = entityCreated.getBody();

        ResponseEntity<Repository> entityRetrieved = this.restTemplate.getForEntity("/api/repository/" + repoCreated.getUuid(),
                Repository.class);

        assertThat(entityRetrieved.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(entityRetrieved.getBody().getName()).isEqualTo("jgraphlib-example");
    }
}
