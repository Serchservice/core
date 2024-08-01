package com.serch.server.services.trip.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.trip.requests.QuotationRequest;
import com.serch.server.services.trip.requests.TripAcceptRequest;
import com.serch.server.services.trip.requests.TripCancelRequest;
import com.serch.server.services.trip.requests.TripInviteRequest;
import com.serch.server.services.trip.responses.TripResponse;

import java.util.List;

public interface TripRequestService {
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
    ApiResponse<TripResponse> invite(TripInviteRequest request);

    /**
     * Request a new trip.
     *
     * @param request the trip request details
     * @param linkId The Shared Link id
     * @param guestId The guest id
     *
     * @return the response containing the active trip details
     *
     * @see TripInviteRequest
     * @see ApiResponse
     * @see TripResponse
     */
    ApiResponse<TripResponse> invite(String guestId, String linkId, TripInviteRequest request);

    /**
     * Send a quote for a trip.
     *
     * @param request the quote request details
     *
     * @return the response containing the active trip details
     *
     * @see QuotationRequest
     * @see ApiResponse
     * @see TripResponse
     */
    ApiResponse<TripResponse> sendQuotation(QuotationRequest request);

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
     * Cancel a trip request.
     *
     * @param request the trip cancel request details
     *
     * @see TripCancelRequest
     * @see ApiResponse
     */
    ApiResponse<String> cancel(TripCancelRequest request);

    /**
     * Cancel a trip quotation.
     *
     * @param request the trip request details
     * @param quoteId The quotation id to be cancelled
     *
     * @see TripCancelRequest
     * @see ApiResponse
     */
    ApiResponse<String> cancel(TripCancelRequest request, Long quoteId);

    /**
     * Trip request history.
     *
     * @param linkId The guest link id (optional)
     * @param guestId The guest id (optional)
     *
     * @return {@link ApiResponse} list of {@link TripResponse}
     *
     * @see ApiResponse
     * @see TripResponse
     */
    ApiResponse<List<TripResponse>> history(String guestId, String linkId);
}
