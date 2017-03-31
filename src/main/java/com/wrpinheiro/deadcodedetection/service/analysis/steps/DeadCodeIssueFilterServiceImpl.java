package com.wrpinheiro.deadcodedetection.service.analysis.steps;

import static com.wrpinheiro.deadcodedetection.model.AnalysisInformation.Stage.FILTERING_AND_SORTING_ISSUES;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import com.wrpinheiro.deadcodedetection.model.AnalysisInformation;
import com.wrpinheiro.deadcodedetection.model.DeadCodeIssue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

/**
 * Implementation of the service to filter and sort a list of dead code issues.
 *
 * @author wrpinheiro
 */
@Slf4j
@Service
public class DeadCodeIssueFilterServiceImpl implements DeadCodeIssueFilterService {
    @Value("#{'${app.analyzer.kindBlacklist}'.toLowerCase().split(';')}")
    private List<String> kindBlacklist;

    @Value("#{'${app.analyzer.kindWhitelist}'.toLowerCase().split(';')}")
    private List<String> kindWhitelist;

    @Value("#{'${app.analyzer.refBlackList}'.toLowerCase().split(';')}")
    private List<String> refBlackList;

    /**
     * Default constructor added to simplify unit tests.
     */
    public DeadCodeIssueFilterServiceImpl() {
    }

    /**
     * Constructor used to allow easier unit tests.
     *
     * @param kindBlacklist the kind blacklist
     * @param kindWhitelist the kind whitelist
     * @param refBlackList the ref blacklist
     */
    public DeadCodeIssueFilterServiceImpl(final List<String> kindBlacklist, final List<String> kindWhitelist,
                                          final List<String> refBlackList) {
        this.kindBlacklist = kindBlacklist;
        this.kindWhitelist = kindWhitelist;
        this.refBlackList = refBlackList;
    }

    /**
     * @see DeadCodeIssueFilterService#filterValidIssuesAndSort(String, AnalysisInformation, List).
     */
    public List<DeadCodeIssue> filterValidIssuesAndSort(final String uuid,
                                                        final AnalysisInformation analysisInformation,
                                                        final List<DeadCodeIssue> deadCodeIssues) {
        analysisInformation.setStage(FILTERING_AND_SORTING_ISSUES);

        log.info("Filtering and sorting issues for repository {}", uuid);

        final Comparator<DeadCodeIssue> deadCodeIssueComparator = comparing(DeadCodeIssue::getFilename)
                .thenComparing(DeadCodeIssue::getKind);

        final List<DeadCodeIssue> filteredIssues = deadCodeIssues.stream()
                .filter(this::isValidKindAndRef)
                .sorted(deadCodeIssueComparator).collect(toList());

        log.debug("Filtered issues:\n" + filteredIssues.stream().map(DeadCodeIssue::toString)
                .collect(joining("\n")));

        return filteredIssues;
    }

    /**
     * <p>Check if a dead code issue is valid based on kind and ref.
     *
     * An issue is considered valid:
     * * kind does not contain any word in the kind blacklist
     * * kind contains at least one word in the kind whitelis
     * * ref does not contain any word in the ref blacklist.</p>
     *
     * @param deadCodeIssue the issue to be checked
     * @return true if the issue is valid or false otherwise
     */
    private boolean isValidKindAndRef(final DeadCodeIssue deadCodeIssue) {
        final String kind = deadCodeIssue.getKind().toLowerCase();
        final String ref = deadCodeIssue.getRef().toLowerCase();

        return !kindBlacklist.stream().anyMatch(kindBlacklisted -> kind.contains(kindBlacklisted))
                && kindWhitelist.stream().anyMatch(kindWhitelisted -> kind.contains(kindWhitelisted))
                && !refBlackList.stream().anyMatch(refBlacklisted -> ref.contains(refBlacklisted));
    }

}
