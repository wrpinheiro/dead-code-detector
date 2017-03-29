package com.wrpinheiro.deadcodedetection.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Model that represents a Github repository.
 *
 * @author wrpinheiro
 */
@JsonDeserialize(builder = GithubRepository.GithubRepositoryBuilder.class)
@Data
@Builder
public class GithubRepository {
    private String url;
    private Language language;
    private String branch;

    /**
     * Return the owner of the repository.
     *
     * @return the owner of the repository.
     */
    public String getOwner() {
        Pattern pattern = pattern = Pattern.compile(".*[/:](.*)/.*\\.git");

        Matcher matcher = pattern.matcher(url);

        return matcher.find()
                ? matcher.group(1) :
                null;
    }

    /**
     * Return the name of the repository.
     *
     * @return the name of the repository.
     */
    public String getName() {
        Pattern pattern = pattern = Pattern.compile(".*[/:].*/(.*)\\.git");

        Matcher matcher = pattern.matcher(url);

        return matcher.find()
                ? matcher.group(1) :
                null;
    }

    /**
     * This is a hack to allow dynamically instantiate the POJO without using the builder.
     */
    @JsonPOJOBuilder(withPrefix = "")
    public static final class GithubRepositoryBuilder {
    }
}
