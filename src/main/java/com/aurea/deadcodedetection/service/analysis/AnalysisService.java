package com.aurea.deadcodedetection.service.analysis;

import com.aurea.deadcodedetection.model.Repository;
import org.springframework.scheduling.annotation.Async;

/**
 * Created by wrpinheiro on 3/24/17.
 */
public interface AnalysisService {
    @Async
    void analyse(Repository repository);
}
