package com.wrpinheiro.deadcodedetection.service.analysis;

import com.wrpinheiro.deadcodedetection.model.Repository;
import org.springframework.scheduling.annotation.Async;

/**
 * Created by wrpinheiro on 3/24/17.
 */
public interface AnalysisService {
    @Async
    void analyze(Repository repository);
}
