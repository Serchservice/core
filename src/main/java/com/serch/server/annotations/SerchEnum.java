package com.serch.server.annotations;

import com.serch.server.annotations.implementations.EnumValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * The SerchEnum annotation is used to mark fields that should be validated by the EnumValidator class.
 * It specifies that the annotated element (typically a field) should only accept values that are instances of Enum types.
 * This annotation is typically applied to fields in DTOs (Data Transfer Objects) or domain objects
 * where only certain enumerated values are acceptable inputs.
 *
 * @see EnumValidator
 */
@Target(value = ElementType.FIELD)
@Retention(value = RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EnumValidator.class)
@Documented
public @interface SerchEnum {

    /**
     * Defines the default error message that will be used when the validation fails.
     *
     * @return The default error message.
     */
    String message() default "Input must be an enum sub";

    /**
     * Groups for validating constraints.
     *
     * @return The validation groups.
     */
    Class<?>[] groups() default {};

    /**
     * Payload for validator-specific response.
     *
     * @return The payload.
     */
    Class<? extends Payload>[] payload() default {};
}