package com.serch.server.services.subscription.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.subscription.PlanType;
import com.serch.server.models.subscription.PlanParent;
import com.serch.server.services.subscription.responses.PlanParentResponse;

import java.util.List;

/**
 * This is the wrapper class for PlanImplementation.
 *
 * @see PlanImplementation
 */
public interface PlanService {
    /**
     * Retrieves all available subscription plans.
     * @return An API response containing a list of all available subscription plans.
     */
    ApiResponse<List<PlanParentResponse>> getPlans();
    /**
     * Retrieves a specific subscription plan based on the provided plan type.
     * @param type The type of the plan to retrieve.
     * @return An API response containing the details of the specified plan.
     */
    ApiResponse<PlanParentResponse> getPlan(PlanType type);
    /**
     * Updates the children of the parent plan in the response.
     * @param parent The parent plan whose children to update.
     * @param response The response object to update with children.
     */
    void updateChildren(PlanParent parent, PlanParentResponse response);
}