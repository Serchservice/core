package com.serch.server.services.trip.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.trip.requests.*;
import com.serch.server.services.trip.responses.PriceDiscussionResponse;

/**
 * This is the wrapper class for the implementation of Shared Trip actions.
 *
 * @see com.serch.server.services.trip.services.implementations.ProvideSharedTripImplementation
 */
public interface ProvideSharedTripService {
    /**
     * This initiates the discussion between a guest and a provider included in the shared link.
     *
     * @param guestId The guest id
     * @param linkId The Shared Link id
     *
     * @return ApiResponse of PriceDiscussionResponse
     *
     * @see ApiResponse
     * @see PriceDiscussionResponse
     */
    ApiResponse<PriceDiscussionResponse> options(String guestId, String linkId);

    /**
     * This continues the conversation between the provider and the guest
     *
     * @param request The {@link PriceChatRequest} request model needed
     *
     * @return ApiResponse of PriceDiscussionResponse
     *
     * @see ApiResponse
     * @see PriceDiscussionResponse
     */
    ApiResponse<PriceDiscussionResponse> chat(PriceChatRequest request);

    /**
     * This changes the amount suggested by both provider and guest
     *
     * @param amount The amount suggested
     * @param guestId The guest id
     * @param linkId The Shared link id
     *
     * @return ApiResponse of string
     *
     * @see ApiResponse
     */
    ApiResponse<String> change(Integer amount, String guestId, String linkId);

    /**
     * This initiates the trip request from a guest to a provider
     *
     * @param request The {@link ProvideSharedTripRequest} request model
     *
     * @return ApiResponse of String
     *
     * @see ApiResponse
     */
    ApiResponse<String> request(ProvideSharedTripRequest request);

    /**
     * This cancels an existing trip request from a guest
     *
     * @param request The {@link ProvideSharedTripCancelRequest} request model
     *
     * @return ApiResponse of string
     *
     * @see ApiResponse
     */
    ApiResponse<String> cancel(ProvideSharedTripCancelRequest request);

    /**
     * This ends an active trip
     *
     * @param tripId The trip id
     * @param guestId The guest id
     *
     * @return ApiResponse of string
     *
     * @see ApiResponse
     */
    ApiResponse<String> end(String tripId, String guestId);
}
