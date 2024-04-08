package com.serch.server.services.subscription.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class VerifySubscriptionRequest {
    private String reference;
    
    @JsonProperty("email_address")
    private String emailAddress;
}
