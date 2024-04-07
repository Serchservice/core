package com.serch.server.services.payment.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PaymentVerificationResponseData {
    private Long id;
    private String status;
    private String reference;
    private Integer amount;
    private String message;
    private String channel;
    private String currency;

    @JsonProperty("ip_address")
    private String ipAddress;

    private Integer fees;
    private PaymentAuthorization authorization;
}
