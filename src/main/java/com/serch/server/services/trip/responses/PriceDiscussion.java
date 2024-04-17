package com.serch.server.services.trip.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.enums.chat.MessageStatus;
import lombok.Data;

@Data
public class PriceDiscussion {
    private String message;
    private MessageStatus status;
    private String label;

    @JsonProperty("is_provider")
    private Boolean isProvider;
}
