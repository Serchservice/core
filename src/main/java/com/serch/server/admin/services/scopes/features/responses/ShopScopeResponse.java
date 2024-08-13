package com.serch.server.admin.services.scopes.features.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.admin.services.responses.CommonProfileResponse;
import com.serch.server.services.shop.responses.ShopResponse;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShopScopeResponse{
    private ShopResponse shop;
    private CommonProfileResponse profile;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}