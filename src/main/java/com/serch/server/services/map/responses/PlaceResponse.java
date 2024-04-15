package com.serch.server.services.map.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * Represents a response containing place details.
 *
 * @see PlaceResult
 */
@Data
public class PlaceResponse {
    private PlaceResult result;
    private String status;

    /**
     * Represents the result of a place search.
     *
     * @see AddressComponent
     */
    @Data
    public static class PlaceResult {
        @JsonProperty("address_components")
        private List<AddressComponent> addressList;

        @JsonProperty("formatted_address")
        private String address;

        private Geometry geometry;

        @JsonProperty("place_id")
        private String placeId;
    }

    /**
     * Represents an address component.
     */
    @Data
    public static class AddressComponent {
        @JsonProperty("long_name")
        private String longName;

        @JsonProperty("short_name")
        private String shortName;
    }

    /**
     * Represents geometry information.
     *
     * @see PlaceLocation
     */
    @Data
    public static class Geometry {
        private PlaceLocation location;
    }

    /**
     * Represents the location of a place.
     */
    @Data
    public static class PlaceLocation {
        private Double lat;
        private Double lng;
    }
}