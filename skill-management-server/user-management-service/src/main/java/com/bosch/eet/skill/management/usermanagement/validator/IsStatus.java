package com.bosch.eet.skill.management.usermanagement.validator;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ FIELD })
@Retention(RUNTIME)
@Constraint(validatedBy = StatusValidator.class)
@Documented
public @interface IsStatus {

    String message() default "Must be 'Active' or 'Inactive'";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
