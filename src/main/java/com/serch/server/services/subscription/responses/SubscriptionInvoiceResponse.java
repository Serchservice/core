package com.serch.server.services.subscription.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.enums.subscription.PlanStatus;
import com.serch.server.services.transaction.responses.AssociateTransactionData;
import lombok.Data;

import java.util.List;

@Data
public class SubscriptionInvoiceResponse {
    private Long id;
    private String subscription;
    private Integer size;
    private String amount;
    private String plan;
    private String image;
    private String mode;
    private String reference;
    private PlanStatus status;

    @JsonProperty("subscribed_at")
    private String subscribedAt;

    @JsonProperty("expired_at")
    private String expiredAt;
    private List<AssociateTransactionData> associates;
}
