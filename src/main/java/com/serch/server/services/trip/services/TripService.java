package com.serch.server.services.trip.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.models.trip.Trip;
import com.serch.server.services.trip.requests.*;
import com.serch.server.services.trip.responses.TripHistoryResponse;

import java.util.List;

/**
 * This is the wrapper class for the implementation of trip actions and activities.
 * <p></p>
 * @see com.serch.server.services.trip.services.implementations.TripImplementation
 * @see com.serch.server.services.trip.controllers.TripController
 */
public interface TripService {
    /**
     * This initiates a request for trip session. Accessible to Users.
     *
     * @param request The request data containing the address of the user making the request.
     *
     * @return ApiResponse of string
     *
     * @see ApiResponse
     * @see TripRequest
     */
    ApiResponse<String> request(TripRequest request);

    /**
     * This accepts an existing trip request. Accessible to Providers.
     *
     * @param request The request data containing the address of the providing accepting the request.
     *
     * @return ApiResponse of string
     *
     * @see ApiResponse
     * @see TripAcceptRequest
     */
    ApiResponse<String> accept(TripAcceptRequest request);

    /**
     * This declines an existing trip request. Accessible to Providers.
     *
     * @param request The request data containing the trip id and reason for declining.
     *
     * @return ApiResponse of string
     *
     * @see ApiResponse
     * @see TripDeclineRequest
     */
    ApiResponse<String> decline(TripDeclineRequest request);

    /**
     * This cancels an existing trip request. Accessible to Users.
     *
     * @param request The request data containing the reason for cancelling and trip id.
     *
     * @return ApiResponse of string
     *
     * @see ApiResponse
     * @see TripCancelRequest
     */
    ApiResponse<String> cancel(TripCancelRequest request);

    /**
     * This makes it possible for users to permit providers into sharing the current trip they're on.
     *
     * @param tripId The trip id of the active trip
     *
     * @return ApiResponse of string
     *
     * @see ApiResponse
     */
    ApiResponse<String> permitSharing(String tripId);

    /**
     * This initiates a request to invite another provider for an existing trip session. Accessible to Providers.
     *
     * @param request The request data containing the trip id and the id of the provider being invited.
     *
     * @return ApiResponse of string
     *
     * @see ApiResponse
     * @see TripInviteRequest
     */
    ApiResponse<String> invite(TripInviteRequest request);

    /**
     * This cancels an existing request to invite another provider to an existing trip session.
     * Accessible to Users.
     *
     * @param request The request data containing the trip id and cancel reason.
     *
     * @return ApiResponse of string
     *
     * @see ApiResponse
     * @see TripCancelRequest
     */
    ApiResponse<String> cancelInvite(TripCancelRequest request);

    /**
     * This makes it possible for providers to announce arrival and verify the code they have, in order
     * to start the trip properly.
     *
     * @param code The authentication code in the provider's phone
     * @param tripId The trip id of the active trip
     *
     * @return ApiResponse of string
     *
     * @see ApiResponse
     */
    ApiResponse<String> announceArrival(String code, String tripId);

    /**
     * This makes it possible for providers to leave the trip.
     *
     * @param tripId The trip id of the active trip
     *
     * @return ApiResponse of string
     *
     * @see ApiResponse
     */
    ApiResponse<String> leave(String tripId);

    /**
     * This makes it possible for either user or provider to end the active trip.
     *
     * @param tripId The trip id of the active trip.
     * @param amount The amount spent during the trip.
     *
     * @return ApiResponse of string
     *
     * @see ApiResponse
     */
    ApiResponse<String> end(String tripId, Integer amount);

    /**
     * This returns a list of trips the logged-in user or provider has been a part of.
     *
     * @return ApiResponse of List for {@link TripHistoryResponse}
     *
     * @see ApiResponse
     */
    ApiResponse<List<TripHistoryResponse>> history();

    /**
     * This cancels the trip
     *
     * @param trip The current active trip {@link Trip}
     * @param reason The reason for trip cancellation
     *
     * @return ApiResponse of string
     */
    ApiResponse<String> getCancelResponse(Trip trip, String reason);

    /**
     * This updates the trip details when it is ended
     *
     * @param amount The amount spent during the trip
     * @param trip The current trip {@link Trip}
     */
    void updateTripWhenEnded(Integer amount, Trip trip);
}
