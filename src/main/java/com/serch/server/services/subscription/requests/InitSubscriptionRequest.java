package com.serch.server.services.subscription.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class InitSubscriptionRequest {
    @JsonProperty("email_address")
    private String emailAddress;

    @JsonProperty("plan")
    private String plan;

    @JsonProperty("child")
    private String child;

    @JsonProperty("callback_url")
    private String callbackUrl;

    @JsonProperty("size")
    private Integer size = 1;
}
