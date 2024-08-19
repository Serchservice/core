package com.serch.server.services.trip.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.core.payment.responses.InitializePaymentData;
import com.serch.server.models.trip.Trip;
import com.serch.server.services.trip.responses.TripResponse;
import com.serch.server.services.trip.services.implementations.TripHistoryImplementation;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * This is the wrapper class for the implementation of trip actions and activities.
 * <p></p>
 * @see TripHistoryImplementation
 * @see com.serch.server.services.trip.controllers.TripController
 */
public interface TripHistoryService {
    /**
     * Prepare the response for an invited trip
     *
     * @param userId The user who is requesting for data
     * @param id The {@link com.serch.server.models.trip.TripInvite} id
     *
     * @return {@link TripResponse}
     */
    TripResponse response(String id, String userId);

    /**
     * Prepare the response from trip data
     *
     * @param userId The user who is requesting for data
     * @param id The {@link Trip} id
     * @param sendUpdate Whether to send socket update all accounts present to the trip
     * @param payment The {@link InitializePaymentData} data (Nullable)
     *
     * @return {@link TripResponse}
     */
    TripResponse response(String id, String userId, @Nullable InitializePaymentData payment, boolean sendUpdate, String requestedId);

    /**
     * This returns a list of pending trips the logged-in user or provider.
     *
     * @param userId The user id
     * @param linkId A nullable shared link id
     * @param guestId A nullable guest id
     *
     * @return List of {@link TripResponse}
     */
    List<TripResponse> inviteHistory(String guestId, UUID userId, String linkId);

    /**
     * This returns a list of trips the logged-in user or provider has been a part of.
     *
     * @param trip The trip id
     * @param sendUpdate Whether to send socket update all accounts present to the trip
     * @param linkId A nullable shared link id
     * @param guest A nullable guest id
     *
     * @return ApiResponse of List for {@link TripResponse}
     *
     * @see ApiResponse
     */
    ApiResponse<List<TripResponse>> history(String guest, String linkId, boolean sendUpdate, String trip);

    /**
     * This returns a list of trips the logged-in user or provider has been a part of.
     *
     * @param linkId A nullable shared link id
     * @param guest A nullable guest id
     *
     * @return ApiResponse of List for {@link TripResponse}
     *
     * @see ApiResponse
     */
    ApiResponse<List<TripResponse>> history(String guest, String linkId);
}
