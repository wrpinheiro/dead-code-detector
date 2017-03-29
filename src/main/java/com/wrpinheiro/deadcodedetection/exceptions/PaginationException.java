package com.wrpinheiro.deadcodedetection.exceptions;

/**
 * Operation that can' be completed due to an invalid state of some resource.
 *
 * @author wrpinheiro
 */
public class PaginationException extends Exception {
    public PaginationException(final String message) {
        super(message);
    }
}
