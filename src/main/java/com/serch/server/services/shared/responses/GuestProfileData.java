package com.serch.server.services.shared.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GuestProfileData {
    @JsonProperty("email_address")
    private String emailAddress;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("fcm_token")
    private String fcmToken;

    private String gender;
    private String id;
    private String avatar;
}
