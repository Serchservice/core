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

        @JsonProperty("place")
        private String place;

        @JsonProperty("placeId")
        private String placeId;

        @JsonProperty("text")
        private Text text;

        @JsonProperty("structuredFormat")
        private StructuredFormat structuredFormat;

        @JsonProperty("types")
        private List<String> types;
    }

    @Data
    public static class Text {

        @JsonProperty("text")
        private String text;

        @JsonProperty("matches")
        private List<Match> matches;
    }

    @Data
    public static class StructuredFormat {

        @JsonProperty("mainText")
        private MainText mainText;

        @JsonProperty("secondaryText")
        private SecondaryText secondaryText;
    }

    @Data
    public static class MainText {

        @JsonProperty("text")
        private String text;

        @JsonProperty("matches")
        private List<Match> matches;
    }

    @Data
    public static class SecondaryText {

        @JsonProperty("text")
        private String text;
    }

    @Data
    public static class Match {

        @JsonProperty("endOffset")
        private int endOffset;
    }
}