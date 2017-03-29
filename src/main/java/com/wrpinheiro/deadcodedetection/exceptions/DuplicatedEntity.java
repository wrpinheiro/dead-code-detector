package com.wrpinheiro.deadcodedetection.exceptions;

/**
 * Indicates an operation that duplicates an entity.
 *
 * @author wrpinheiro
 */
public class DuplicatedEntity extends RuntimeException {
    public DuplicatedEntity(final String msg) {
        super(msg);
    }
}
