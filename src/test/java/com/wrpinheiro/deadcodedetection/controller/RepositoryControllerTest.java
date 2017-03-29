package com.wrpinheiro.deadcodedetection.controller;

import static org.assertj.core.api.Assertions.assertThat;

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

/**
 * Test endpoint of Repository controller.
 *
 * @author wrpinheiro
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RepositoryControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;

    // GET /api/repository
    @Test
    public void mustReturnAListOfRepositories() {
        final ResponseEntity<Object> entity = this.restTemplate.getForEntity("/api/repository",
                Object.class);

        assertThat(entity.getBody()).isNotNull();
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    // POST /api/repository
    @Test
    public void mustReturnErrorDueToMissingNameAndUrl() {
        final RepositoryRequest request = new RepositoryRequest();
        final ResponseEntity<Map> entity = this.restTemplate.postForEntity("/api/repository",
                request, Map.class);

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.PRECONDITION_FAILED);
    }

    // POST /api/repository
    @Test
    public void mustCreateARepositoryWithBranchAndDefaultLanguage() {
        final RepositoryRequest request = new RepositoryRequest();

        request.setUrl("https://github.com/wrpinheiro/jgraphlib.git");

        final ResponseEntity<Repository> entity = this.restTemplate.postForEntity("/api/repository",
                request, Repository.class);

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(entity.getBody().getGithubRepository().getUrl())
                .isEqualTo("https://github.com/wrpinheiro/jgraphlib.git");
        assertThat(entity.getBody().getGithubRepository().getLanguage()).isEqualTo(Language.JAVA);
        assertThat(entity.getBody().getGithubRepository().getBranch()).isEqualTo("master");
    }

    // POST /api/repository/{uuid}
    @Test
    public void mustReturnRepositoryJustCreated() {
        final String url = "https://github.com/wrpinheiro/jgraphlib.git";
        final RepositoryRequest request = new RepositoryRequest();

        request.setUrl(url);

        final ResponseEntity<Repository> entityCreated = this.restTemplate.postForEntity("/api/repository",
                request, Repository.class);

        assertThat(entityCreated.getStatusCode()).isEqualTo(HttpStatus.OK);

        final Repository repoCreated = entityCreated.getBody();
        final String uuid = repoCreated.getUuid();

        final ResponseEntity<Repository> entityRetrieved = this.restTemplate.getForEntity("/api/repository/"
                        + repoCreated.getUuid(), Repository.class);

        assertThat(entityRetrieved.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(entityRetrieved.getBody().getUuid()).isEqualTo(uuid);
        assertThat(entityRetrieved.getBody().getGithubRepository().getUrl()).isEqualTo(url);
    }
}
