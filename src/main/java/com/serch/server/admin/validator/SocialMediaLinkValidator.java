package com.serch.server.admin.validator;

import com.serch.server.admin.enums.SocialMediaPlatform;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SocialMediaLinkValidator implements ConstraintValidator<ValidSocialMediaLink, String> {
    private SocialMediaPlatform platform;

    @Override
    public void initialize(ValidSocialMediaLink constraintAnnotation) {
        this.platform = constraintAnnotation.platform();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // Allow empty values
        }

        String regex;
        switch (platform) {
            case INSTAGRAM:
                regex = "https?://(www\\.)?instagram\\.com/.*";
                break;
            case TWITTER:
                regex = "https?://(www\\.)?twitter\\.com/.*";
                break;
            case LINKEDIN:
                regex = "https?://(www\\.)?linkedin\\.com/.*";
                break;
            default:
                return false;
        }

        return value.matches(regex);
    }
}
