package com.serch.server.domains.nearby.services.addon.responses;

import com.serch.server.enums.nearby.GoAddonType;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GoAddonResponse {
    private Long id;
    private String name;
    private String description;
    private GoAddonType type;
    private List<GoAddonPlanResponse> plans = new ArrayList<>();
}