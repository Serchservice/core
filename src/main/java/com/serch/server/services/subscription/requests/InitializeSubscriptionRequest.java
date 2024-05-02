package com.serch.server.services.subscription.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Data
@Getter
@Setter
public class InitializeSubscriptionRequest {
    @JsonProperty("plan")
    private String plan;

    @JsonProperty("callback_url")
    private String callbackUrl;

    private List<UUID> associates;
}