package com.serch.server.admin.services.organization.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

import java.io.Serializable;

/**
 * DTO for {@link com.serch.server.admin.models.Organization}
 */
public record OrganizationDto(
        @NotBlank(message = "Username cannot be empty")
        String username,

        @Email(message = "Email address must be properly formatted and end with @serchservice.com", regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@serchservice\\.com$")
        @JsonProperty("email_address")
        String emailAddress,

        @NotBlank(message = "First name cannot be empty")
        @JsonProperty("first_name")
        String firstName,

        @NotBlank(message = "Last name cannot be empty")
        @JsonProperty("last_name")
        String lastName,

        @NotBlank(message = "Phone number cannot be empty")
        @JsonProperty("phone_number")
        String phoneNumber,

        @NotBlank(message = "Avatar cannot be empty")
        String avatar,

        @URL(
                regexp = "^$|https?://(www\\.)?instagram\\.com/.*",
                message = "Instagram link must be a valid URL starting with https://instagram.com or be empty"
        )
        String instagram,

        @URL(
                regexp = "^$|https?://(www\\.)?(twitter\\.com|x\\.com)/.*",
                message = "Twitter link must be a valid URL starting with https://twitter.com, https://x.com, or be empty"
        )
        String twitter,

        @URL(
                regexp = "^$|https?://(www\\.)?linkedin\\.com/.*",
                message = "LinkedIn link must be a valid URL starting with https://linkedin.com or be empty"
        )
        String linkedIn,

        @NotBlank(message = "Position in Serchservice cannot be empty")
        String position
) implements Serializable { }