package com.wrpinheiro.deadcodedetection.exceptions;

/**
 * Operation that can' be completed due to an invalid state of some resource.
 *
 * @author wrpinheiro
 */
public class InvalidStateException extends RuntimeException {
    public InvalidStateException(String message) {
        super(message);
    }
}
