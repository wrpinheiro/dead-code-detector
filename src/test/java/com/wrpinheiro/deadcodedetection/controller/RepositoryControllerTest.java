package com.wrpinheiro.deadcodedetection.controller;

import static com.wrpinheiro.deadcodedetection.model.RepositoryStatus.ADDED;
import static com.wrpinheiro.deadcodedetection.model.RepositoryStatus.COMPLETED;
import static com.wrpinheiro.deadcodedetection.model.RepositoryStatus.FAILED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import com.wrpinheiro.deadcodedetection.controller.dto.RepositoryRequest;
import com.wrpinheiro.deadcodedetection.model.Language;
import com.wrpinheiro.deadcodedetection.model.Paginator;
import com.wrpinheiro.deadcodedetection.model.Repository;
import com.wrpinheiro.deadcodedetection.model.RepositoryStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
    private static final long INTERVAL_BETWEEN_FIND_CALLS = 3000;

    private static final int MAX_CALLS_ATEMPTS = 10;

    private static final int DEFAULT_PAGINATOR_PAGE_SIZE = 10;

    private static final String DCD_EXAMPLE_GITHUB_URL = "https://github.com/wrpinheiro/dcd-example.git";

    @Autowired
    private RepositoryClient client;

    // GET /api/repository
    @Test
    public void mustReturnAListOfRepositories() {
        final Paginator paginator = client.findAllRepositories().getBody();

        assertThat(paginator).isNotNull();
        assertThat(paginator.getPage()).isEqualTo(1);
        assertThat(paginator.getPageSize()).isEqualTo(DEFAULT_PAGINATOR_PAGE_SIZE);
    }

    // POST /api/repository
    @Test
    public void mustReturnPreConditionFailedErrorWhenTryingToCreateARepositoryWithoutURL() {
        final RepositoryRequest request = new RepositoryRequest();

        final ResponseEntity<Map> entity = client.postToRepository(request);

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.PRECONDITION_FAILED);
    }

    // POST /api/repository
    @Test
    public void mustCreateARepositoryWithBranchDefaultAndLanguageDefault() {
        final ResponseEntity<Repository> entity = client.createRepository(DCD_EXAMPLE_GITHUB_URL);

        assertThat(entity.getBody().getGithubRepository().getUrl())
                .isEqualTo(DCD_EXAMPLE_GITHUB_URL);
        assertThat(entity.getBody().getGithubRepository().getLanguage()).isEqualTo(Language.JAVA);
        assertThat(entity.getBody().getGithubRepository().getBranch()).isEqualTo("master");
    }

    // POST /api/repository
    @Test
    public void mustCreateRepositoryWithBranchDifferentFromMaster() {
        final ResponseEntity<Repository> entity = client.createRepository(DCD_EXAMPLE_GITHUB_URL, "example-classes");

        assertThat(entity.getBody().getGithubRepository().getUrl())
                .isEqualTo(DCD_EXAMPLE_GITHUB_URL);
        assertThat(entity.getBody().getGithubRepository().getLanguage()).isEqualTo(Language.JAVA);
        assertThat(entity.getBody().getGithubRepository().getBranch()).isEqualTo("example-classes");
    }

    // POST /api/repository
    // GET /api/repository/{uuid}
    @Test
    public void mustReturnRepositoryJustCreated() {
        final ResponseEntity<Repository> entityCreated = client.createRepository(DCD_EXAMPLE_GITHUB_URL);

        final Repository repoCreated = entityCreated.getBody();
        final String uuid = repoCreated.getUuid();

        final ResponseEntity<Repository> entityRetrieved = client.findRepositoryByUUID(uuid);

        assertThat(entityRetrieved.getBody().getUuid()).isEqualTo(uuid);
        assertThat(entityRetrieved.getBody().getGithubRepository().getUrl()).isEqualTo(DCD_EXAMPLE_GITHUB_URL);
    }

    // POST /api/repository
    // GET /api/repository/{uuid}
    @Test
    public void mustAddRepositoryAndTriggerTheAnalyzer() {
        try {
            Repository repository = client.createRepository(DCD_EXAMPLE_GITHUB_URL).getBody();

            final String uuid = repository.getUuid();

            RepositoryStatus repositoryStatus = repository.getStatus();

            int attempt = 0;

            while (repositoryStatus == ADDED && attempt++ < MAX_CALLS_ATEMPTS) {
                repository = client.findRepositoryByUUID(uuid).getBody();
                repositoryStatus = repository.getStatus();

                Thread.sleep(INTERVAL_BETWEEN_FIND_CALLS);
            }

            assertThat(repositoryStatus).isNotEqualTo(ADDED);
        } catch (InterruptedException ex) {
            fail(ex.getMessage());
        }
    }

    // POST /api/repository
    // GET /api/repository/{uuid}
    @Test
    public void mustAddARepositoryAndRunTheAnalyzerWithSuccess() {
        try {
            Repository repository = client.createRepository(DCD_EXAMPLE_GITHUB_URL).getBody();

            final String uuid = repository.getUuid();

            RepositoryStatus repositoryStatus = repository.getStatus();

            int attempt = 0;

            while (repositoryStatus != FAILED && repositoryStatus != COMPLETED && attempt++ < MAX_CALLS_ATEMPTS) {
                repository = client.findRepositoryByUUID(uuid).getBody();
                repositoryStatus = repository.getStatus();

                Thread.sleep(INTERVAL_BETWEEN_FIND_CALLS);
            }

            assertThat(repositoryStatus).isEqualTo(COMPLETED);
        } catch (InterruptedException ex) {
            fail(ex.getMessage());
        }
    }
}
