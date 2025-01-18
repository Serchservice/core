package com.serch.server.domains.trip.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.domains.trip.requests.TripAcceptRequest;
import com.serch.server.domains.trip.requests.TripAuthRequest;
import com.serch.server.domains.trip.requests.TripCancelRequest;
import com.serch.server.domains.trip.requests.TripShareRequest;
import com.serch.server.domains.trip.responses.TripResponse;

import java.util.List;

/**
 * TripShareService interface defines the operations related to sharing trips
 * among providers and guests.
 */
public interface TripShareService {

    /**
     * Grants or denies access for the provider to share a trip with a guest.
     *
     * @param guest The guest ID (if the guest is the one participating in the trip).
     * @param id The trip ID to which access is being granted or denied.
     *
     * @return {@link ApiResponse} containing the {@link TripResponse}
     *         which reflects the updated trip access status.
     */
    ApiResponse<TripResponse> access(String guest, String id);

    /**
     * Allows the provider to share a trip with others.
     *
     * @param request The {@link TripShareRequest} containing the details of the trip to be shared.
     *
     * @return {@link ApiResponse} containing the {@link TripResponse}
     *         representing the shared trip details.
     */
    ApiResponse<TripResponse> share(TripShareRequest request);

    /**
     * Cancels an existing shared trip invite.
     *
     * @param request The {@link TripCancelRequest} containing the details needed to cancel the invite.
     *
     * @return {@link ApiResponse} indicating the success or failure of the cancellation.
     */
    ApiResponse<String> cancel(TripCancelRequest request);

    /**
     * Accepts a shared trip invite.
     *
     * @param request The {@link TripAcceptRequest} containing the details of the trip acceptance.
     *
     * @return {@link ApiResponse} containing the {@link TripResponse}
     *         for the accepted trip.
     */
    ApiResponse<TripResponse> accept(TripAcceptRequest request);

    /**
     * Verifies the authentication for a trip, ensuring that the requestor is authorized.
     *
     * @param request The trip authentication request details contained in {@link TripAuthRequest}.
     *
     * @return {@link ApiResponse} containing the {@link TripResponse}
     *         that includes the active trip details upon successful authentication.
     *
     * @see TripAuthRequest
     * @see ApiResponse
     * @see TripResponse
     */
    ApiResponse<TripResponse> auth(TripAuthRequest request);

    /**
     * Ends a trip from the shared standpoint, notifying all participants.
     *
     * @param id The ID of the trip to be ended, represented by {@link com.serch.server.models.trip.Trip}.
     *
     * @return {@link ApiResponse} containing a list of {@link TripResponse}
     *         that reflects the status of the ended trip.
     */
    ApiResponse<List<TripResponse>> end(String id);

    /**
     * Leaves a shared trip from the participant's standpoint.
     *
     * @param id The ID of the shared trip, represented by {@link com.serch.server.models.trip.TripShare}.
     *
     * @return {@link ApiResponse} containing a list of {@link TripResponse}
     *         that reflects the remaining participants in the trip after leaving.
     */
    ApiResponse<List<TripResponse>> leave(String id);
}