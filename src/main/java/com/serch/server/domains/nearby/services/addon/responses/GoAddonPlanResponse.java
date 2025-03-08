package com.serch.server.domains.nearby.services.addon.responses;

import com.serch.server.enums.nearby.GoAddonPlanInterval;
import lombok.Data;

@Data
public class GoAddonPlanResponse {
    private String id;
    private String name;
    private String description;
    private String amount;
    private GoAddonPlanInterval interval;
    private String currency;
}