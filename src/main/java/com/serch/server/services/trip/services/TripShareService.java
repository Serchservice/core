package com.serch.server.services.trip.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.trip.requests.TripAcceptRequest;
import com.serch.server.services.trip.requests.TripAuthRequest;
import com.serch.server.services.trip.requests.TripCancelRequest;
import com.serch.server.services.trip.requests.TripShareRequest;
import com.serch.server.services.trip.responses.TripResponse;

import java.util.List;

public interface TripShareService {
    /**
     * Grant access or deny access for the provider to share the trip
     *
     * @param guest The guest id (If the guest is the one in the trip)
     * @param id The trip id
     *
     * @return {@link ApiResponse} of {@link TripResponse}
     */
    ApiResponse<TripResponse> access(String guest, String id);

    /**
     * The provider can share the trip
     *
     * @param request The {@link TripShareRequest} data
     *
     * @return {@link ApiResponse} of {@link TripResponse}
     */
    ApiResponse<TripResponse> share(TripShareRequest request);

    /**
     * Cancel the shared trip invite
     *
     * @param request The {@link TripCancelRequest} data
     *
     * @return {@link ApiResponse}
     */
    ApiResponse<String> cancel(TripCancelRequest request);

    /**
     * Accept shared trip
     *
     * @param request The {@link TripAcceptRequest} data
     *
     * @return {@link ApiResponse} of {@link TripResponse}
     */
    ApiResponse<TripResponse> accept(TripAcceptRequest request);

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
     * End trip from the shared standpoint
     *
     * @param id The {@link com.serch.server.models.trip.Trip} id
     *
     * @return {@link ApiResponse} list of {@link TripResponse}
     */
    ApiResponse<List<TripResponse>> end(String id);

    /**
     * Leave trip from the shared standpoint
     *
     * @param id The shared trip id. {@link com.serch.server.models.trip.TripShare}
     *
     * @return {@link ApiResponse} list of {@link TripResponse}
     */
    ApiResponse<List<TripResponse>> leave(String id);
}