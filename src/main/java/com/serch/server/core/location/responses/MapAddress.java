package com.serch.server.core.location.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.bases.BaseLocation;
import lombok.Data;

import java.util.List;

@Data
public class MapAddress {
    @JsonProperty("formattedAddress")
    private String formattedAddress;

    @JsonProperty("addressComponents")
    private List<AddressComponent> addressComponents;

    @JsonProperty("location")
    private BaseLocation location;

    @JsonProperty("displayName")
    private DisplayName displayName;

    @JsonProperty("shortFormattedAddress")
    private String shortFormattedAddress;

    @Data
    public static class AddressComponent {
        @JsonProperty("longText")
        private String longText;

        @JsonProperty("shortText")
        private String shortText;

        @JsonProperty("types")
        private List<String> types;

        @JsonProperty("languageCode")
        private String languageCode;
    }

    @Data
    public static class DisplayName {
        @JsonProperty("text")
        private String text;

        @JsonProperty("languageCode")
        private String languageCode;
    }
}
