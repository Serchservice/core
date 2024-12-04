package com.serch.server.admin.services.scopes.account.responses.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.services.shop.responses.ShopOverviewResponse;
import com.serch.server.services.shop.responses.ShopServiceResponse;
import com.serch.server.services.shop.responses.ShopWeekdayResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class AccountUserScopeShopResponse extends ShopOverviewResponse {
    @JsonProperty("created_at")
    private ZonedDateTime createdAt;

    @JsonProperty("updated_at")
    private ZonedDateTime updatedAt;

    private Weekday current;
    private List<Weekday> weekdays;
    private List<Service> services;

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class Service extends ShopServiceResponse {
        @JsonProperty("created_at")
        private ZonedDateTime createdAt;

        @JsonProperty("updated_at")
        private ZonedDateTime updatedAt;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class Weekday extends ShopWeekdayResponse {
        @JsonProperty("created_at")
        private ZonedDateTime createdAt;

        @JsonProperty("updated_at")
        private ZonedDateTime updatedAt;
    }
}
