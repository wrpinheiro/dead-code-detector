package com.wrpinheiro.deadcodedetection.service.analysis;

import com.wrpinheiro.deadcodedetection.exceptions.AnalysisException;
import com.wrpinheiro.deadcodedetection.model.Repository;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.wrpinheiro.deadcodedetection.model.AnalysisInformation.Stage.CLONING_REPO;
import static java.util.Comparator.reverseOrder;

/**
 * Implementation of Github services
 *
 * Created by wrpinheiro on 3/26/17.
 */
@Slf4j
@Service
public class GithubServiceImpl implements GithubService {
    @Value("${app.analyzer.dataDir}")
    private String dataDir;

    private static final String REPOSITORIES_SUBDIR = "repos/";

    public Path cloneGitHubRepository(Repository repository) {
        log.info("Cloning repository {}", repository.getGithubRepository().getUrl());

        repository.getLastAnalysisInformation().setStage(CLONING_REPO);

        try {
            File repositoryDir = this.createRepositoryDirectory(repository.getName());

            log.info("Repository {} will be cloned to directory {}", repository.getGithubRepository().getUrl(),
                    repositoryDir.getAbsolutePath());

            Git.cloneRepository()
                    .setBare(false)
                    .setURI(repository.getGithubRepository().getUrl())
                    .setDirectory(repositoryDir)
                    .setCloneSubmodules(false)
                    .setBranch(repository.getGithubRepository().getBranch())
                    .call();

            // the analysis doesn't required the .git directory.
            removeDotGitDir(repositoryDir);

            return repositoryDir.toPath();
        } catch(GitAPIException | JGitInternalException | IOException ex) {
            log.error("Error downloading Github repository with message {}", ex);
            throw new AnalysisException(ex.getMessage());
        }
    }

    private File createRepositoryDirectory(String name) throws IOException {
        String repositoriesDirectory = dataDir + REPOSITORIES_SUBDIR;

        Path path = Paths.get(repositoriesDirectory, name + "/");

        if (Files.exists(path)) {
            deleteSubDirectoryStructure(path);
        }

        Files.createDirectories(path);

        return Paths.get(repositoriesDirectory, name).toFile();
    }

    private void removeDotGitDir(File repositoryDir) throws IOException {
        Path gitDir = Paths.get(repositoryDir.getAbsolutePath(), ".git");
        deleteSubDirectoryStructure(gitDir);
    }

    private void deleteSubDirectoryStructure(Path path) throws IOException {
        Files.walk(path, FileVisitOption.FOLLOW_LINKS)
                .sorted(reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }
}
