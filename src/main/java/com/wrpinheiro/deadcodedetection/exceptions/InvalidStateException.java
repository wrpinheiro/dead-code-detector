package com.wrpinheiro.deadcodedetection.exceptions;

/**
 * Created by wrpinheiro on 3/25/17.
 */
public class InvalidStateException extends RuntimeException {
    public InvalidStateException(String message) {
        super(message);
    }
}
