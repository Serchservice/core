package com.serch.server.services.subscription.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class InitializeSubscriptionRequest {
    @JsonProperty("plan")
    private String plan;

    @JsonProperty("callback_url")
    private String callbackUrl;
}