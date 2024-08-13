package com.serch.server.services.trip.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.models.account.Profile;
import com.serch.server.models.trip.Trip;
import com.serch.server.services.trip.requests.*;
import com.serch.server.services.trip.responses.ActiveResponse;
import com.serch.server.services.trip.responses.TripResponse;

import java.util.List;

/**
 * TripService interface defines the operations related to trip management.
 */
public interface TripService {
    /**
     * Request a new trip.
     *
     * @param request the trip request details
     *
     * @return the response containing the active trip details
     *
     * @see TripInviteRequest
     * @see ApiResponse
     * @see TripResponse
     */
    ApiResponse<TripResponse> request(TripInviteRequest request);

    /**
     * Recreate an existing trip data into a new one
     *
     * @param withInvited Whether to use the invited provider as the main provider in the new created trip
     * @param id The trip id to use in recreating the same trip data
     *
     * @return {@link ApiResponse} of {@link TripResponse} data
     */
    ApiResponse<TripResponse> rebook(String id, Boolean withInvited);


    /**
     * Create a trip from schedule
     *
     * @param request The {@link TripInviteRequest} data
     * @param account The {@link Profile} data of the user
     * @param profile The {@link Profile} data of the provider
     *
     * @return {@link ApiResponse} of requested {@link TripResponse}
     */
    ApiResponse<TripResponse> request(TripInviteRequest request, Profile account, Profile profile);

    /**
     * Accept a trip request.
     *
     * @param request the trip accept request details
     *
     * @return the response containing the active trip details
     *
     * @see TripAcceptRequest
     * @see ApiResponse
     * @see TripResponse
     */
    ApiResponse<TripResponse> accept(TripAcceptRequest request);

    /**
     * End an active trip.
     *
     * @param request The {@link TripCancelRequest} data
     *
     * @see ApiResponse
     */
    ApiResponse<List<TripResponse>> end(TripCancelRequest request);

    /**
     * Leave an active trip.
     *
     * @param id the trip id
     *
     * @see ApiResponse
     * @see TripResponse
     */
    ApiResponse<List<TripResponse>> leave(String id);

    /**
     * Search for active providers based on the provided phone number.
     *
     * @param lng Provider lng
     * @param lat Provider latitude
     * @param phoneNumber Nullable phone number
     *
     * @return the response containing a list of active responses
     * @see ApiResponse
     * @see ActiveResponse
     */
    ApiResponse<List<ActiveResponse>> search(String phoneNumber, Double lat, Double lng);

    /**
     * Verify trip authentication.
     *
     * @param request the trip authentication request details
     *
     * @return the response containing the active trip details
     *
     * @see TripAuthRequest
     * @see ApiResponse
     * @see TripResponse
     */
    ApiResponse<TripResponse> auth(TripAuthRequest request);

    /**
     * Cancel the trip
     *
     * @param request The {@link TripCancelRequest} data
     *
     * @return {@link ApiResponse} list of {@link TripResponse}
     */
    ApiResponse<List<TripResponse>> cancel(TripCancelRequest request);

    /**
     * Try paying the service fee, if not paid
     *
     * @param id The trip id
     *
     * @return {@link ApiResponse} list of {@link TripResponse}
     */
    ApiResponse<TripResponse> payServiceFee(String id);

    /**
     * Verify the transaction for special trip requests
     *
     * @param id The trip id
     * @param guest The guest id (Optional)
     * @param reference The transaction reference
     *
     * @return {@link ApiResponse} list of {@link TripResponse}
     */
    ApiResponse<TripResponse> verify(String id, String guest, String reference);

    /**
     * Update the details of a trip.
     *
     * @param request the trip update request details
     * @see TripUpdateRequest
     *
     * @return {@link ApiResponse} list of {@link TripResponse}
     */
    ApiResponse<TripResponse> update(TripUpdateRequest request);

    /**
     * Update other entities data in the trip
     *
     * @param trip The {@link Trip} data
     */
    void updateOthers(Trip trip);

    /**
     * Update the details of a provider location.
     *
     * @param request the trip update request details
     * @see MapViewRequest
     */
    void update(MapViewRequest request);
}