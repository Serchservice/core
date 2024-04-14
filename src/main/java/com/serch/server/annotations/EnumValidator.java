package com.serch.server.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * The EnumValidator class implements the ConstraintValidator interface to validate
 * whether a given value is an enumeration type or not, based on the SerchEnum annotation.
 * <p></p>
 * It checks if the value is not null, and if it's an instance of Enum
 * {@link EnumValidator#isValid(Object, ConstraintValidatorContext)}.
 * <p></p>
 * This validator is typically used in conjunction with SerchEnum annotation to validate
 * fields that should only accept values from a predefined set of enums.
 *
 * @see ConstraintValidator
 * @see SerchEnum
 */
public class EnumValidator implements ConstraintValidator<SerchEnum, Object> {

    /**
     * Initializes the validator.
     *
     * @param constraintAnnotation The annotation instance for SerchEnum.
     */
    @Override
    public void initialize(SerchEnum constraintAnnotation) {
        // No initialization logic needed in this validator.
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    /**
     * Validates whether the given value is an instance of Enum.
     *
     * @param value                    The value to be validated.
     * @param constraintValidatorContext Context in which the constraint is evaluated.
     * @return true if the value is an instance of Enum, false otherwise.
     */
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
        // Null values are considered invalid.
        if (value == null) {
            return false;
        }
        // Checks if the value is an instance of Enum.
        return value instanceof Enum<?>;
    }
}

