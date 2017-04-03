package com.wrpinheiro.deadcodedetection.validation;

/**
 * @author wrpinheiro
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a validation error.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidationError {
    private String message;
    private String invalidValue;
}
