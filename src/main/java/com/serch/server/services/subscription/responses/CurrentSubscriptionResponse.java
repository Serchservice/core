package com.serch.server.services.subscription.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class CurrentSubscriptionResponse {
    private String id;
    private String plan;
    private String child;
    private String description;

    @JsonProperty("is_active")
    private Boolean isActive;

    private String remaining;
    private String status;
    private String color;
    private String image;
    private String amount;
    private String duration;
    private Integer size;
    private List<String> benefits;
    private SubscriptionCardResponse card;
}
