package com.wrpinheiro.deadcodedetection.service.analysis.steps;

import com.wrpinheiro.deadcodedetection.model.AnalysisInformation;
import com.wrpinheiro.deadcodedetection.model.GithubRepository;

import java.nio.file.Path;

/**
 * Interface to Scitools UND command.
 *
 * @author wrpinheiro
 */
public interface UnderstandService {

    Path createUDBFile(final String uuid, final GithubRepository githubRepository,
                              final AnalysisInformation analysisInformation, final Path repositoryDir);
}
