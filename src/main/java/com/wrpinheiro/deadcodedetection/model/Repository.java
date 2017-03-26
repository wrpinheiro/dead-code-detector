package com.wrpinheiro.deadcodedetection.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wrpinheiro on 3/21/17.
 */
@Data
@Builder
public class Repository {
    private Long id;
    private AnalysisStatus status;
    private String errorMessage;

    /**
     * The time the repository was added
     */
    private Date createdAt;

    /**
     * The time the repository was processed
     */
    private Date processedAt;
    private String url;
    private Language language;
    private String repositoryDescription;
    private List<DeadCodeIssue> deadCodeIssues;

    public String getOwner() {
        Pattern pattern = pattern = Pattern.compile(".*[/:](.*)/.*\\.git");

        Matcher matcher = pattern.matcher(url);

        return matcher.find() ?
            matcher.group(1) :
            null;
    }

    public String getName() {
        Pattern pattern = pattern = Pattern.compile(".*[/:].*/(.*)\\.git");

        Matcher matcher = pattern.matcher(url);

        return matcher.find() ?
                matcher.group(1) :
                null;
    }

    public void setStatus(AnalysisStatus status) {
        this.status = status;

        if (!status.equals(AnalysisStatus.FAILED)) {
            this.errorMessage = null;
        }

        if (!status.equals(AnalysisStatus.COMPLETED)) {
            this.deadCodeIssues = null;
        }
    }
}
