package com.wrpinheiro.deadcodedetection.service.analysis;

import static com.wrpinheiro.deadcodedetection.model.AnalysisInformation.Stage.CLONING_REPO;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Comparator.reverseOrder;

import com.wrpinheiro.deadcodedetection.exceptions.AnalysisException;
import com.wrpinheiro.deadcodedetection.model.GithubRepository;
import com.wrpinheiro.deadcodedetection.model.Repository;
import com.wrpinheiro.deadcodedetection.service.analysis.ProcessUtils.ProcessOutput;
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
import java.util.concurrent.TimeoutException;

/**
 * Implementation of Github services.
 *
 * @author wrpinheiro
 */
@Slf4j
@Service
public class GithubServiceImpl implements GithubService {
    /**
     * Max time without activity before a Git command throws a timeout.
     */
    @Value("${app.analyzer.gitTransportTimeout}")
    private int gitTransportTimeout;

    @Value("${app.analyzer.dataDir}")
    private String dataDir;

    private static final String REPOSITORIES_SUBDIR = "repos/";

    /**
     * Clone a Github repository.
     *
     * @param repository repository with information the repo that will be cloned
     * @return the path to the local repository
     */
    public Path cloneGitHubRepository(final Repository repository) {
        final GithubRepository githubRepository = repository.getGithubRepository();

        log.info("Cloning repository {}", githubRepository.getUrl());

        repository.getLastAnalysisInformation().setStage(CLONING_REPO);

        try {
            isRemoteRefAvailable(githubRepository);

            final File repositoryDir = cloneRepository(repository.getUuid(), githubRepository);

            return repositoryDir.toPath();
        } catch (GitAPIException | JGitInternalException | IOException ex) {
            log.error("Error downloading Github repository with message {}", ex);
            throw new AnalysisException(ex.getMessage(), ex);
        }
    }

    private File cloneRepository(final String uuid, final GithubRepository githubRepository) throws GitAPIException,
            IOException {
        try {
            final File repositoryDir = this.createRepositoryDirectory(uuid, githubRepository.getName());

            log.info("Repository {} will be cloned to directory {}", githubRepository.getUrl(),
                    repositoryDir.getAbsolutePath());

            log.info("Downloading repo tarball: " + "set -euo pipefail; wget -qO- --no-check-certificate "
                    + "https://github.com/" + githubRepository.getOwner() + "/"
                    + githubRepository.getName() + "/archive/" + githubRepository.getBranch()
                    + ".tar.gz | tar -zxC " + repositoryDir.getAbsolutePath() + " --strip-components 1");

            final ProcessUtils.ProcessCommand command = ProcessUtils.ProcessCommand.builder().commands(asList(
                    "/bin/bash",  "-c", "set -euo pipefail; wget -qO- --no-check-certificate "
                            + "https://github.com/" + githubRepository.getOwner()
                            + "/" + githubRepository.getName() + "/archive/" + githubRepository.getBranch()
                            + ".tar.gz | tar -zxC " + repositoryDir.getAbsolutePath() + " --strip-components 1"))
                    .timeout(120).build();

            final ProcessOutput output = ProcessUtils.runProcess(command);

            if (output.getExitCode() != 0) {
                log.info("Error cloning repository: {}.", uuid);

                final String logs = format("\n\nStdout: %s\n\nStderr: %s\n\n", output.getStdout(),
                        output.getStderr());
                log.error("Error creating UDB file\n{}", logs);

                throw new AnalysisException("Error cloning repository: " + githubRepository.getUrl());
            }

            log.info("Finished cloning repository: {}", githubRepository.getUrl());

            return repositoryDir;
        } catch (InterruptedException | TimeoutException ex) {
            log.error("Error cloning Github repository with message {}", ex);
            throw new AnalysisException(ex.getMessage(), ex);
        }
    }

    /**
     * Run a ls-remote to check if the branch or tag is available.
     * @param githubRepository the information about the repository to ls
     * @throws GitAPIException a general exception trying to access the repository
     * @throws AnalysisException when the branch chosen by user doesn't exist in the remote repository
     */
    private void isRemoteRefAvailable(final GithubRepository githubRepository) throws GitAPIException,
            AnalysisException {
        final Collection<Ref> refs = Git.lsRemoteRepository()
                .setRemote(githubRepository.getUrl()).call();

        if (!refs.stream().anyMatch(ref -> ref.getName().endsWith(githubRepository.getBranch()))) {
            throw new AnalysisException(format("Could not find branch %s in repository %s",
                    githubRepository.getBranch(), githubRepository.getUrl()));
        }
    }

    private File createRepositoryDirectory(final String uuid, final String repositoryName) throws IOException {
        final String repositoriesDirectory = dataDir + REPOSITORIES_SUBDIR;

        final Path path = Paths.get(repositoriesDirectory, uuid, repositoryName);

        if (Files.exists(path)) {
            deleteSubDirectoryStructure(path);
        }

        Files.createDirectories(path);

        return path.toFile();
    }

    private void deleteSubDirectoryStructure(final Path path) throws IOException {
        Files.walk(path, FileVisitOption.FOLLOW_LINKS)
                .sorted(reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }
}
