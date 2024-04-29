package com.serch.server.services.auth.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RequestBusinessProfile {
    private String name;
    private String description;
    private String address;
    private String contact;

    @JsonProperty("email_address")
    private String emailAddress;
    private RequestDevice device;
}
