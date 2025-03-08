package com.serch.server.domains.nearby.services.addon.responses;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class GoAddonVerificationResponse extends GoAddonResponse {
    private GoAddonPlanResponse activator;
}