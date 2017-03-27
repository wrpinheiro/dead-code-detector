package com.wrpinheiro.deadcodedetection.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wrpinheiro on 3/26/17.
 */
@JsonDeserialize(builder = GithubRepository.GithubRepositoryBuilder.class)
@Data
@Builder
public class GithubRepository {
    private String url;
    private Language language;
    private String branch;

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

    /**
     * This is a hack to allow dynamically instantiate the POJO without using the builder.
     */
    @JsonPOJOBuilder(withPrefix = "")
    public static final class GithubRepositoryBuilder {
    }
}
