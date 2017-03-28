package com.wrpinheiro.deadcodedetection.exceptions;

/**
 * @author wrpinheiro
 */
public class InvalidStateException extends RuntimeException {
    public InvalidStateException(String message) {
        super(message);
    }
}
