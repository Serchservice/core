package com.serch.server.admin.validator;

import com.serch.server.admin.enums.SocialMediaPlatform;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SocialMediaLinkValidator.class)
public @interface ValidSocialMediaLink {
    String message() default "Invalid social media link";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    SocialMediaPlatform platform();
}