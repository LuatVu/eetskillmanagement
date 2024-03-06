package com.bosch.eet.skill.management.usermanagement.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.bosch.eet.skill.management.common.Status;

public class StatusValidator implements ConstraintValidator<IsStatus, String> {

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if(s == null) return true;
        return Status.INACTIVE.getLabel().equals(s) || Status.ACTIVE.getLabel().equals(s);
    }
}
