package com.serch.server.services.auth.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RequestBusinessProfile {
    private String name;
    private String description;
    private String address;
    private String contact;
    private Double latitude;
    private Double longitude;
    private String place;
    private String state;
    private String country;

    @JsonProperty("email_address")
    private String emailAddress;
    private RequestDevice device;
}