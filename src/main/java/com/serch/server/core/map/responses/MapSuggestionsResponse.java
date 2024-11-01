package com.serch.server.core.map.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class MapSuggestionsResponse {
    @JsonProperty("suggestions")
    private List<Suggestion> suggestions;

    @Data
    public static class Suggestion {
        @JsonProperty("placePrediction")
        private PlacePrediction placePrediction;
    }

    @Data
    public static class PlacePrediction {
        @JsonProperty("placeId")
        private String placeId;
    }
}