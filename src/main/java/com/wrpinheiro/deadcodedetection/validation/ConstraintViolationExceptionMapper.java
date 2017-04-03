package com.wrpinheiro.deadcodedetection.validation;

import jersey.repackaged.com.google.common.collect.Lists;

import org.glassfish.jersey.server.validation.internal.ValidationHelper;

import java.util.List;

import javax.annotation.Priority;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.Priorities;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Custom constraint violation exception mapper.
 *
 * @author wrpinheiro
 */
@Provider
@Priority(Priorities.USER)
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {
    /**
     * Generates a response with a custom error message.
     *
     * @param constraintViolationException the constraint violation
     * @return a response
     */
    @Override
    public Response toResponse(final ConstraintViolationException constraintViolationException) {
        final Response.ResponseBuilder response = Response.status(ValidationHelper.getResponseStatus(
                    constraintViolationException));

        response.type(MediaType.APPLICATION_JSON_TYPE);

        response.entity(
            new GenericEntity<>(
                constraintViolationToValidationErrors(constraintViolationException),
                new GenericType<List<ValidationError>>() {}.getType()
            )
        );

        return response.build();
    }

    private static List<ValidationError> constraintViolationToValidationErrors(
            final ConstraintViolationException violation) {
        return Lists.transform(Lists.newArrayList(violation.getConstraintViolations()),
            violation1 -> new ValidationError(
                    violation1.getMessage(),
                    violation1.getInvalidValue().toString()
            ));
    }
}
