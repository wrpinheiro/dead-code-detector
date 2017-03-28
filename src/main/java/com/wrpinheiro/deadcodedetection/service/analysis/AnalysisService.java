package com.wrpinheiro.deadcodedetection.service.analysis;

import com.wrpinheiro.deadcodedetection.model.Repository;
import org.springframework.scheduling.annotation.Async;

/**
 * @author wrpinheiro
 */
public interface AnalysisService {
    @Async
    void analyze(Repository repository);
}
