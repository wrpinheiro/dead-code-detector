package com.wrpinheiro.deadcodedetection.exceptions;

/**
 * Created by wrpinheiro on 3/25/17.
 */
public class DuplicatedEntity extends RuntimeException {
    public DuplicatedEntity(String msg) {
        super(msg);
    }
}
