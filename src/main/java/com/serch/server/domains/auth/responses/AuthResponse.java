package com.serch.server.domains.auth.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("has_mfa")
    private Boolean mfaEnabled;

    @JsonProperty("has_recovery_codes")
    private Boolean recoveryCodesEnabled;

    private UUID id;
    private String role;
    private String category;
    private String image;
    private Double rating;
    private String avatar;
    private SessionResponse session;
}