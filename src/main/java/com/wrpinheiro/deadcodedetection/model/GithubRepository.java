package com.wrpinheiro.deadcodedetection.model;

import lombok.Builder;
import lombok.Data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wrpinheiro on 3/26/17.
 */
@Data
@Builder
public class GithubRepository {
    private String url;
    private Language language;

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
}
