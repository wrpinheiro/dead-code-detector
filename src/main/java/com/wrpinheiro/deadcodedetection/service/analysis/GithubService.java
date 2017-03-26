package com.wrpinheiro.deadcodedetection.service.analysis;

import com.wrpinheiro.deadcodedetection.model.Repository;

import java.nio.file.Path;

/**
 * Created by wrpinheiro on 3/26/17.
 */
public interface GithubService {
    /**
     * Clone a Github repository and return the Path where it was created.
     * @param repository repository with information the repo that will be cloned
     * @return the Path where the repository was cloned
     */
    Path cloneGitHubRepository(Repository repository);
}
