package com.serch.server.domains.nearby.bases;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GoBaseUserResponse {
    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("email_address")
    private String emailAddress;

    private String contact;
    private String avatar;
}