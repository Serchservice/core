package com.serch.server.services.business.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.business.responses.BusinessAssociateResponse;

import java.util.List;
import java.util.UUID;

/**
 * This is the wrapper class that contains the methods performed in BusinessSubscriptionService.
 *
 * @see com.serch.server.services.business.services.implementations.BusinessSubscriptionImplementation
 */
public interface BusinessSubscriptionService {
    /**
     * Fetches the list of associates a business wants to subscribe for
     *
     * @return List of {@link BusinessAssociateResponse}
     *
     * @see ApiResponse
     */
    ApiResponse<List<BusinessAssociateResponse>> subscriptions();

    /**
     * Adds an associate provider to the list of business subscriptions
     *
     * @param id The id of the associate provider
     *
     * @return List of {@link BusinessAssociateResponse}
     *
     * @see ApiResponse
     */
    ApiResponse<List<BusinessAssociateResponse>> add(UUID id);

    /**
     * Adds all associate providers to the list of business subscriptions
     *
     * @return List of {@link BusinessAssociateResponse}
     *
     * @see ApiResponse
     */
    ApiResponse<List<BusinessAssociateResponse>> addAll();

    /**
     * Suspends an associate provider from business subscription
     *
     * @param id The id pf the associate provider
     *
     * @return List of {@link BusinessAssociateResponse}
     *
     * @see ApiResponse
     */
    ApiResponse<List<BusinessAssociateResponse>> suspend(UUID id);

    /**
     * Removes an associate provider from the list of business subscriptions
     *
     * @param id The id of the associate provider
     *
     * @return List of {@link BusinessAssociateResponse}
     *
     * @see ApiResponse
     */
    ApiResponse<List<BusinessAssociateResponse>> remove(UUID id);
}