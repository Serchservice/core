package com.serch.server.services.trip.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PriceChatRequest {
    private String message;
    private String sender;

    @JsonProperty("guest_id")
    private String guestId;

    @JsonProperty("link_id")
    private String linkId;
}
