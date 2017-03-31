package com.wrpinheiro.deadcodedetection.service.analysis;

import com.wrpinheiro.deadcodedetection.model.AnalysisInformation;
import com.wrpinheiro.deadcodedetection.model.DeadCodeIssue;
import com.wrpinheiro.deadcodedetection.model.Repository;
import com.wrpinheiro.deadcodedetection.model.RepositoryStatus;
import com.wrpinheiro.deadcodedetection.service.analysis.algorithms.DeadCodeAnalyzer;
import com.wrpinheiro.deadcodedetection.service.analysis.steps.DeadCodeIssueFilterService;
import com.wrpinheiro.deadcodedetection.service.analysis.steps.DeadCodeParserService;
import com.wrpinheiro.deadcodedetection.service.analysis.steps.GithubService;
import com.wrpinheiro.deadcodedetection.service.analysis.steps.UnderstandService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.List;

/**
 * Implementation of analysis services.
 *
 * @author wrpinheiro
 */
@Slf4j
@Service
public class AnalysisServiceImpl implements AnalysisService {

    @Value("${app.analyzer.dataDir}")
    private String dataDir;

    @Value("${app.analyzer.scriptsDir}")
    private String scriptsDir;

    @Value("${app.analyzer.scitoolsHome}")
    private String scitoolsHome;

    @Autowired
    private GithubService githubService;

    @Autowired
    private UnderstandService understandService;

    @Autowired
    private DeadCodeAnalyzer deadCodeAnalyzer;

    @Autowired
    private DeadCodeParserService deadCodeParserService;

    @Autowired
    private DeadCodeIssueFilterService deadCodeIssueFilterService;

    @Override
    @Async
    public void analyze(final Repository repository) {
        final AnalysisInformation analysisInformation = new AnalysisInformation();

        try {
            log.info("Starting analysis for repository {}", repository.getUuid());

            analysisInformation.startAnalysis();

            repository.startingAnalysis(analysisInformation);

            final Path repositoryDir = githubService.cloneGitHubRepository(repository);
            final Path udbFile = understandService. createUDBFile(repository.getUuid(),
                    repository.getGithubRepository(), analysisInformation, repositoryDir);
            final String deadCodeIssuesOutput = deadCodeAnalyzer.findDeadCodeIssues(repository.getUuid(),
                    analysisInformation, udbFile);

            final List<DeadCodeIssue> issues = deadCodeParserService.parse(repository.getUuid(),
                    repository.getGithubRepository().getName(), repository.getLastAnalysisInformation(),
                    deadCodeIssuesOutput);

            final List<DeadCodeIssue> filteredIssues = deadCodeIssueFilterService.filterValidIssuesAndSort(
                    repository.getUuid(), repository.getLastAnalysisInformation(), issues);

            analysisInformation.finishAnalysis(filteredIssues);

            repository.finishAnalysis();

            log.info("Finished analysis for repository {}", repository.getUuid());
        } catch (Exception ex) {
            log.info("Error analyzing repository {}", repository.getUuid());
            log.debug("Error analyzing repository", ex);
            analysisInformation.setErrorMessage(ex.getMessage());
            repository.setStatus(RepositoryStatus.FAILED);
        }
    }
}
