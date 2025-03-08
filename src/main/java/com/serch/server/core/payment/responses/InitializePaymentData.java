package com.serch.server.core.payment.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class InitializePaymentData {
    @JsonProperty("authorization_url")
    private String authorizationUrl;

    @JsonProperty("access_code")
    private String accessCode;

    private String reference;
}
