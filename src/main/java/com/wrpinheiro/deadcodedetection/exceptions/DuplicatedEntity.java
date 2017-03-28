package com.wrpinheiro.deadcodedetection.exceptions;

/**
 * @author wrpinheiro
 */
public class DuplicatedEntity extends RuntimeException {
    public DuplicatedEntity(String msg) {
        super(msg);
    }
}
