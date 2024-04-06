package com.serch.server.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target(value = ElementType.FIELD)
@Retention(value = RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EnumValidator.class)
@Documented()
public @interface SerchEnum {
    String message() default "Input must be an enum sub";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
