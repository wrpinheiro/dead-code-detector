package com.wrpinheiro.deadcodedetection.exceptions;

/**
 * Represents an exception thrown by the analyzer
 *
 * Created by wrpinheiro on 3/24/17.
 */
public class AnalysisException extends RuntimeException {
    public AnalysisException(String message) {
        super(message);
    }

    public AnalysisException(String message, Throwable cause) {
        super(message, cause);
    }
}
