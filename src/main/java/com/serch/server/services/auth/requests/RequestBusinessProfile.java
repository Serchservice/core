package com.serch.server.services.auth.requests;

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
    private String token;
    private RequestDevice device;
}