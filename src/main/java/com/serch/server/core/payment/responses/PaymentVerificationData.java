package com.serch.server.core.payment.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentVerificationData {
    private Long id;
    private String status;
    private String reference;
    private Integer amount;
    private String message;
    private String channel;
    private String currency;
    private Integer fees;

    @JsonProperty("ip_address")
    private String ipAddress;

    private PaymentAuthorization authorization;
    private Customer customer;

    private String plan;

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Customer {
        private String email;
        private Long id;

        @JsonProperty("first_name")
        private String firstName;

        @JsonProperty("last_name")
        private String lastName;

        @JsonProperty("customer_code")
        private String code;
    }
}