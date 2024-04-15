package com.serch.server.services.map.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Represents a prediction for a place search.
 */
@Data
public class Prediction {
    private String description;

    @JsonProperty("place_id")
    private String placeId;

    private String reference;

    @JsonProperty("main_text")
    private String mainText;

    @JsonProperty("secondary_text")
    private String secondaryText;
}