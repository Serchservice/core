package com.serch.server.services.account.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MoreProfileData {
    @JsonProperty("last_signed_in")
    private String lastSignedIn;

    @JsonProperty("number_of_rating")
    private Integer numberOfRating;

    @JsonProperty("number_of_shops")
    private Integer numberOfShops;

    @JsonProperty("total_service_trips")
    private Integer totalServiceTrips;

    @JsonProperty("total_shared")
    private Integer totalShared;

    @JsonProperty("is_enabled")
    private Boolean isEnabled;

    @JsonProperty("is_non_locked")
    private Boolean isNonLocked;

    @JsonProperty("is_non_expired")
    private Boolean isNonExpired;
}
