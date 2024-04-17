package com.serch.server.services.trip.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class PriceDiscussionResponse {
    private String amount;

    @JsonProperty("can_accept")
    private Boolean canAccept;

    private List<PriceDiscussion> discussions;
    private List<String> options;
}
