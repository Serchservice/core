package com.serch.server.core.map.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Address {
    private String id;
    private String place;
    private String country;
    private String state;
    private String city;
    private Double latitude;
    private Double longitude;

    @JsonProperty("local_government_area")
    private String localGovernmentArea;

    @JsonProperty("street_number")
    private String streetNumber;

    @JsonProperty("street_name")
    private String streetName;
}