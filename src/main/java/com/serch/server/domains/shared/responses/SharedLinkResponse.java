package com.serch.server.domains.shared.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SharedLinkResponse {
    @JsonProperty("total_guests")
    private Integer totalGuests;

    private SharedLinkData data;
    private List<GuestProfileData> guests;
}