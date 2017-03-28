package com.wrpinheiro.deadcodedetection.service.analysis;

import com.wrpinheiro.deadcodedetection.exceptions.AnalysisException;
import com.wrpinheiro.deadcodedetection.model.GithubRepository;
import com.wrpinheiro.deadcodedetection.model.Repository;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.lib.Ref;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

import static com.wrpinheiro.deadcodedetection.model.AnalysisInformation.Stage.CLONING_REPO;
import static java.util.Comparator.reverseOrder;

/**
 * Implementation of Github services
 *
 * @author wrpinheiro
 */
@Slf4j
@Service
public class GithubServiceImpl implements GithubService {
    @Value("${app.analyzer.dataDir}")
    private String dataDir;

    private static final String REPOSITORIES_SUBDIR = "repos/";

    public Path cloneGitHubRepository(Repository repository) {
        GithubRepository githubRepository = repository.getGithubRepository();

        log.info("Cloning repository {}", githubRepository.getUrl());

        repository.getLastAnalysisInformation().setStage(CLONING_REPO);

        try {
            isRemoteRefAvailable(githubRepository);

            File repositoryDir = cloneRepository(repository.getUuid(), githubRepository);

            // the analysis doesn't required the .git directory.
            removeDotGitDir(repositoryDir);

            return repositoryDir.toPath();
        } catch(GitAPIException | JGitInternalException | IOException ex) {
            log.error("Error downloading Github repository with message {}", ex);
            throw new AnalysisException(ex.getMessage(), ex);
        }
    }

    private File cloneRepository(String uuid, GithubRepository githubRepository) throws GitAPIException, IOException {
        File repositoryDir = this.createRepositoryDirectory(uuid, githubRepository.getName());

        log.info("Repository {} will be cloned to directory {}", githubRepository.getUrl(),
                repositoryDir.getAbsolutePath());

        Git.cloneRepository()
                .setBare(false)
                .setURI(githubRepository.getUrl())
                .setDirectory(repositoryDir)
                .setCloneSubmodules(false)
                .setBranch(githubRepository.getBranch())
                .call();

        return repositoryDir;
    }

    /**
     * Run a ls-remote to check if the branch or tag is available.
     * @param githubRepository
     * @throws GitAPIException a general exception trying to access the repository
     * @throws AnalysisException when the branch chosen by user doesn't exist in the remote repository
     */
    private void isRemoteRefAvailable(GithubRepository githubRepository) throws GitAPIException, AnalysisException {
        Collection<Ref> refs = Git.lsRemoteRepository()
                .setRemote(githubRepository.getUrl()).call();

        if (!refs.stream().anyMatch(ref -> ref.getName().endsWith(githubRepository.getBranch()))) {
            throw new AnalysisException(String.format("Could not find branch %s in repository %s",
                    githubRepository.getBranch(), githubRepository.getUrl()));
        }
    }

    private File createRepositoryDirectory(String uuid, String name) throws IOException {
        String repositoriesDirectory = dataDir + REPOSITORIES_SUBDIR;

        Path path = Paths.get(repositoriesDirectory, uuid, name);

        if (Files.exists(path)) {
            deleteSubDirectoryStructure(path);
        }

        Files.createDirectories(path);

        return path.toFile();
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
