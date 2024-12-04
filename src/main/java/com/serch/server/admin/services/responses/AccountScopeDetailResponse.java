package com.serch.server.admin.services.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class AccountScopeDetailResponse {
    private String avatar;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("email_address")
    private String emailAddress;

    @JsonProperty("created_at")
    private ZonedDateTime createdAt;

    @JsonProperty("updated_at")
    private ZonedDateTime updatedAt;

    @JsonProperty("account_created_at")
    private String accountCreatedAt = "N/A";

    @JsonProperty("account_updated_at")
    private String accountUpdatedAt = "N/A";

    @JsonProperty("profile_created_at")
    private String profileCreatedAt = "N/A";

    @JsonProperty("profile_updated_at")
    private String profileUpdatedAt = "N/A";

    @JsonProperty("email_confirmed_at")
    private String emailConfirmedAt = "N/A";

    @JsonProperty("password_updated_at")
    private String passwordUpdatedAt = "N/A";

    @JsonProperty("last_signed_in")
    private String lastSignedIn = "N/A";
}