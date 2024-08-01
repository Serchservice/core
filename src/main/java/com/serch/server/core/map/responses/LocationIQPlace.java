package com.serch.server.core.map.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class LocationIQPlace {
    @JsonProperty("place_id")
    private String placeId;

    @JsonProperty("osm_id")
    private String osmId;

    @JsonProperty("osm_type")
    private String osmType;

    private String licence;
    private String lat;
    private String lon;

    @JsonProperty("boundingbox")
    private List<String> boundingBox;

    @JsonProperty("class")
    private String classification;

    private String type;

    @JsonProperty("display_name")
    private String displayName;

    @JsonProperty("display_place")
    private String displayPlace;

    @JsonProperty("display_address")
    private String displayAddress;

    private LocationIQAddress address;

    @Data
    public static class LocationIQAddress {
        private String name;

        @JsonProperty("house_number")
        private String houseNumber;

        private String road;
        private String city;
        private String state;

        @JsonProperty("postcode")
        private String postCode;

        private String country;
    }
}
