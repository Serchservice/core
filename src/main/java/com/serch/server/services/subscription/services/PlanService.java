package com.serch.server.services.subscription.services;

import com.serch.server.bases.ApiResponse;
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
     *
     * @return An API response containing a list of all available subscription plans.
     *
     * @see PlanParentResponse
     * @see ApiResponse
     */
    ApiResponse<List<PlanParentResponse>> plans();
}