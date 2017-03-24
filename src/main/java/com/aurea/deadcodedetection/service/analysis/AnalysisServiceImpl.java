package com.aurea.deadcodedetection.service.analysis;

import com.aurea.deadcodedetection.model.AnalysisStatus;
import com.aurea.deadcodedetection.model.Repository;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Collections.singletonList;
import static java.util.Comparator.reverseOrder;

/**
 * Created by wrpinheiro on 3/24/17.
 */
@Slf4j
@Service
public class AnalysisServiceImpl implements AnalysisService {

    @Value("${app.analyzer.repositoriesDirectory}")
    private String repositoriesDirectory;

    private static final String BRANCH_TO_ANALYZE = "master";

    @Override
    @Async
    public void analyse(Repository repository) {
        log.info("Starting analysis for repository {}", repository.getRepositoryUrl());
        repository.setStatus(AnalysisStatus.PROCESSING);

        try {
            cloneGitHubRepository(repository.getRepositoryUrl());
        } catch (RuntimeException ex) {
            repository.setErrorMessage(ex.getMessage());
            repository.setStatus(AnalysisStatus.FAILED);
        }
    }

    private void cloneGitHubRepository(String url) {
        log.info("Cloning repository {}", url);

        String ownerAndRepoName[] = this.getUserAndNameFromUrl(url);
        String owner = ownerAndRepoName[0];
        String repositoryName = ownerAndRepoName[1];

        try {
            File repositoryDir = this.createRepositoryDirectory(owner, repositoryName);

            Git.cloneRepository()
                    .setBare(false)
                    .setURI(url)
                    .setDirectory(repositoryDir)
                    .setBranchesToClone(singletonList(BRANCH_TO_ANALYZE))
                    .setCloneSubmodules(false)
                    .call();

            // the analysis doesn't required the .git directory.
            removeDotGitDir(repositoryDir, repositoryName);
            new File(repositoryDir, repositoryName).delete();
        } catch(GitAPIException | JGitInternalException | IOException ex) {
            log.error("Error downloading Github repository with message {}", ex.getMessage());
            throw new AnalysisException(ex.getMessage());
        }
    }

    private void removeDotGitDir(File repositoryDir, String repositoryName) throws IOException {
        Path gitDir = Paths.get(repositoryDir.getAbsolutePath(), ".git");
        deleteSubDirectoryStructure(gitDir);
    }

    private String[] getUserAndNameFromUrl(String url) {
        Pattern pattern = pattern = Pattern.compile(".*[/:](.*)/(.*)\\.git");

        Matcher matcher = pattern.matcher(url);

        if (matcher.find()) {
            return new String[]{matcher.group(1), matcher.group(2)};
        } else {
            return null;
        }
    }

    private File createRepositoryDirectory(String owner, String repository) throws IOException {
        Path path = Paths.get(repositoriesDirectory, owner + "/");

        if (Files.exists(path)) {
            deleteSubDirectoryStructure(path);
        }

        Files.createDirectories(path);

        return Paths.get(repositoriesDirectory, owner, repository).toFile();
    }

    private void deleteSubDirectoryStructure(Path path) throws IOException {
        Files.walk(path, FileVisitOption.FOLLOW_LINKS)
                .sorted(reverseOrder())
                .map(Path::toFile)
                .peek(System.out::println)
                .forEach(File::delete);
    }
}
