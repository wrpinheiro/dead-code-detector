package com.wrpinheiro.deadcodedetection.service.analysis.steps;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import com.wrpinheiro.deadcodedetection.model.AnalysisInformation;
import com.wrpinheiro.deadcodedetection.model.DeadCodeIssue;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Unit test for DeadCodeIssueFilterServiceImplTest.
 *
 * @author wrpinheiro
 */
public class DeadCodeIssueFilterServiceImplTest {
    @Ignore("know bug: when refBlackList is empty it will reject every issue!! "
            + "The same must happen to the order black/whitelists")
    public void mustIgnoreEmptyBlacklistRef() {
        final DeadCodeIssueFilterService deadCodeIssueFilterService =
                new DeadCodeIssueFilterServiceImpl(asList("public"),
                        asList("method"), asList(""));

        final DeadCodeIssue privateMethod    = DeadCodeIssue.builder().kind("private method")
                .ref("abc").filename("File1.java").build();

        final List<DeadCodeIssue> filteredIssues =  deadCodeIssueFilterService.filterValidIssuesAndSort("an id",
                new AnalysisInformation(), Arrays.asList(privateMethod));

        assertThat(filteredIssues).hasSize(1);
        assertThat(filteredIssues.get(0).getKind()).isEqualTo("private method");
    }

    @Test
    public void mustRemoveIssueWithKindInBlacklist() {
        final DeadCodeIssueFilterService deadCodeIssueFilterService =
                new DeadCodeIssueFilterServiceImpl(asList("public"),
                        asList("variable", "method"), asList("serialId"));

        final DeadCodeIssue publicMethod    = DeadCodeIssue.builder().kind("PUBLIC MeThoD")
                .ref("abc").filename("File1.java").build();
        final DeadCodeIssue privateVariable = DeadCodeIssue.builder().kind("privAte VaRiAblE")
                .ref("def").filename("File1.java").build();
        final DeadCodeIssue publicInterface = DeadCodeIssue.builder().kind("pUbLiC interface")
                .ref("ghi").filename("File1.java").build();
        final DeadCodeIssue privateMethod = DeadCodeIssue.builder().kind("PRIVate Method")
                .ref("jkl").filename("File1.java").build();

        final List<DeadCodeIssue> issues = asList(publicMethod, privateVariable, publicInterface, privateMethod);

        final List<DeadCodeIssue> filteredIssues =  deadCodeIssueFilterService.filterValidIssuesAndSort("an id",
                new AnalysisInformation(), issues);

        assertThat(filteredIssues).hasSize(2);

        final List<String> kinds = filteredIssues.stream().map(DeadCodeIssue::getKind).collect(toList());
        assertThat(kinds).contains("PRIVate Method", "privAte VaRiAblE");
    }
}
