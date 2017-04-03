package com.wrpinheiro.deadcodedetection.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;

/**
 * Custom validator to check if a URL is a valid Github url.
 *
 * @author wrpinheiro
 */
@Constraint(validatedBy = GithubUrl.GithubUrlValidator.class)
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GithubUrl {
    /**
     * Regex to validate a Github url.
     */
    String regexp() default "^https:\\/\\/github\\.com\\/.*\\/.*\\.git$";

    /**
     * Key for the validation message.
     */
    String message() default "{com.wrpinheiro.deadcodedetection.validation.invalid.github-url}";

    /**
     * The groups this annotation belongs.
     */
    Class<?>[] groups() default {};

    /**
     * The annotation's payload.
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * Custom validator for Github urls.
     */
    class GithubUrlValidator implements ConstraintValidator<GithubUrl, String> {

        private java.util.regex.Pattern pattern;

        @Override
        public void initialize(final GithubUrl gitHubUrl) {
            pattern = java.util.regex.Pattern.compile(gitHubUrl.regexp());
        }

        @Override
        public boolean isValid(final String value, final ConstraintValidatorContext context) {
            return value == null || pattern.matcher(value).matches();
        }
    }
}
