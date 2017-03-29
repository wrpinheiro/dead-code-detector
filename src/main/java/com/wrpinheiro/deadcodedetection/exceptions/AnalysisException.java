package com.wrpinheiro.deadcodedetection.exceptions;

/**
 * Represents an exception thrown by the analyzer.
 *
 * @author wrpinheiro
 */
public class AnalysisException extends RuntimeException {
    public AnalysisException(final String message) {
        super(message);
    }

    public AnalysisException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
