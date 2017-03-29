package com.wrpinheiro.deadcodedetection.service.analysis;

import com.wrpinheiro.deadcodedetection.model.Repository;
import org.springframework.scheduling.annotation.Async;

/**
 * Services for for the analyzer.
 *
 * @author wrpinheiro
 */
public interface AnalysisService {
    /**
     * Analyzes a repository.
     *
     * @param repository the repository to be analyzed
     */
    @Async
    void analyze(Repository repository);
}
