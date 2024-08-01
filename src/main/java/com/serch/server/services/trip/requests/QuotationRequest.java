package com.serch.server.services.trip.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class QuotationRequest {
    private String guest;
    private Integer amount;
    private String id;

    @JsonProperty("quote_id")
    private Long quoteId;
}