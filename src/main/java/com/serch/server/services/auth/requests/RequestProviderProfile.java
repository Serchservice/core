package com.serch.server.services.auth.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class RequestProviderProfile {
    @NotEmpty(message = "First name cannot be empty")
    @JsonProperty(value = "first_name")
    private String firstName;

    @NotEmpty(message = "Last name cannot be empty")
    @JsonProperty(value = "last_name")
    private String lastName;

    @NotEmpty(message = "Gender cannot be empty")
    private String gender;

    @JsonProperty(value = "phone_information")
    RequestPhoneInformation phoneInformation;

    @Pattern(
            regexp = "^[A-Za-z0-9@-Z]",
            message = "Password must contain an uppercase, lowercase and special character"
    )
    @Min(value = 5, message = "Password cannot be less than 5")
    @Max(value = 30, message = "Password cannot be more than 30")
    private String password;

    @JsonProperty(value = "fcm_token")
    private String fcmToken;

    @Email(message = "Email is not properly formatted")
    @JsonProperty(value = "email_address")
    private String emailAddress;

    private String referral;
}
