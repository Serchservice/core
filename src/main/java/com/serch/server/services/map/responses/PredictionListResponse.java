package com.serch.server.services.map.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * Represents a response containing a list of predictions for place searches.
 *
 * @see PlacePrediction
 */
@Data
public class PredictionListResponse {
    private String status;
    private List<PlacePrediction> predictions;

    /**
     * Represents a prediction for a place search.
     *
     * @see FormattedAddress
     */
    @Data
    public static class PlacePrediction {
        private String description;

        @JsonProperty("place_id")
        private String placeId;

        private String reference;

        @JsonProperty("structured_formatting")
        private FormattedAddress address;
    }

    /**
     * Represents a formatted address.
     */
    @Data
    public static class FormattedAddress {
        @JsonProperty("main_text")
        private String mainText;

        @JsonProperty("secondary_text")
        private String secondaryText;
    }
}