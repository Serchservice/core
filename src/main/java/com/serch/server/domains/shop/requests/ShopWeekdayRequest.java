package com.serch.server.domains.shop.requests;

import com.serch.server.enums.shop.Weekday;
import lombok.Data;

@Data
public class ShopWeekdayRequest {
    private Weekday day;
    private String opening;
    private String closing;
}