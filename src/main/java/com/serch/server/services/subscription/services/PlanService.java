package com.serch.server.services.subscription.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.subscription.PlanType;
import com.serch.server.models.subscription.PlanParent;
import com.serch.server.services.subscription.responses.PlanParentResponse;

import java.util.List;

public interface PlanService {
    ApiResponse<List<PlanParentResponse>> getPlans();
    ApiResponse<PlanParentResponse> getPlan(PlanType type);
    void updateChildren(PlanParent parent, PlanParentResponse response);
}