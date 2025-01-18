package com.serch.server.domains.shop.responses;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ShopResponse extends ShopOverviewResponse {
    private String link;
    private ShopWeekdayResponse current;
    private List<ShopWeekdayResponse> weekdays;
    private List<ShopServiceResponse> services;
}