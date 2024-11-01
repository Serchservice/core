package com.serch.server.core.map.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
public class PlacesResponse {
    @JsonProperty("places")
    private List<Place> places;

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class Place extends MapAddress {
        @JsonProperty("id")
        private String id;

        @JsonProperty("nationalPhoneNumber")
        private String nationalPhoneNumber;

        @JsonProperty("internationalPhoneNumber")
        private String internationalPhoneNumber;

        @JsonProperty("googleMapsUri")
        private String googleMapsUri;

        @JsonProperty("businessStatus")
        private String businessStatus;

        @JsonProperty("iconMaskBaseUri")
        private String iconMaskBaseUri;

        @JsonProperty("userRatingCount")
        private Long userRatingCount;

        @JsonProperty("rating")
        private Double rating;

        @JsonProperty("currentOpeningHours")
        private OpeningHours currentOpeningHours;

        @JsonProperty("editorialSummary")
        private Description editorialSummary;

        @JsonProperty("generativeSummary")
        private GenerativeSummary generativeSummary;

        @JsonProperty("googleMapsLinks")
        private GoogleMapsLinks googleMapsLinks;

        @Data
        public static class OpeningHours {
            @JsonProperty("openNow")
            private Boolean openNow;
        }

        @Data
        public static class GenerativeSummary {
            @JsonProperty("description")
            private Description description;

            @JsonProperty("overview")
            private Description overview;
        }

        @Data
        public static class Description {
            @JsonProperty("text")
            private String text;
        }

        @Data
        public static class GoogleMapsLinks {
            @JsonProperty("directionsUri")
            private String directionsUri;

            @JsonProperty("placeUri")
            private String placeUri;

            @JsonProperty("writeAReviewUri")
            private String writeAReviewUri;

            @JsonProperty("reviewsUri")
            private String reviewsUri;

            @JsonProperty("photosUri")
            private String photosUri;
        }
    }
}
