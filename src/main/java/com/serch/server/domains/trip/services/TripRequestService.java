package com.serch.server.domains.trip.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.domains.trip.requests.QuotationRequest;
import com.serch.server.domains.trip.requests.TripAcceptRequest;
import com.serch.server.domains.trip.requests.TripCancelRequest;
import com.serch.server.domains.trip.requests.TripInviteRequest;
import com.serch.server.domains.trip.responses.TripResponse;

/**
 * Service interface for handling trip dtos.
 * <p>
 * This interface provides methods for inviting trips, sending quotations,
 * accepting trip dtos, and managing trip cancellations. It also supports
 * retrieving the trip request history.
 * </p>
 */
public interface TripRequestService {
    /**
     * Invites a new trip based on the provided request details.
     * <p>
     * This method initiates a new trip request using the details specified in
     * the {@link TripInviteRequest}. It processes the invitation and returns
     * the active trip details.
     * </p>
     *
     * @param request The trip invitation request containing the details needed
     *                to create a new trip.
     * @return An {@link ApiResponse} containing the active {@link TripResponse}
     *         details of the trip.
     *
     * @see TripInviteRequest
     * @see ApiResponse
     * @see TripResponse
     */
    ApiResponse<TripResponse> invite(TripInviteRequest request);

    /**
     * Sends a quotation for a trip.
     * <p>
     * This method submits a quotation for a trip based on the details provided
     * in the {@link QuotationRequest}, and returns the response containing the
     * active trip details.
     * </p>
     *
     * @param request The quotation request containing the pricing and details
     *                for the trip.
     * @return An {@link ApiResponse} containing the active {@link TripResponse}
     *         details of the trip.
     *
     * @see QuotationRequest
     * @see ApiResponse
     * @see TripResponse
     */
    ApiResponse<TripResponse> sendQuotation(QuotationRequest request);

    /**
     * Accepts a trip request.
     * <p>
     * This method processes the acceptance of a trip request, using the details
     * provided in the {@link TripAcceptRequest}, and returns the response
     * containing the active trip details.
     * </p>
     *
     * @param request The trip acceptance request containing the necessary details
     *                to accept the trip.
     * @return An {@link ApiResponse} containing the active {@link TripResponse}
     *         details of the trip.
     *
     * @see TripAcceptRequest
     * @see ApiResponse
     * @see TripResponse
     */
    ApiResponse<TripResponse> accept(TripAcceptRequest request);

    /**
     * Cancels a trip request.
     * <p>
     * This method cancels an existing trip based on the details specified in
     * the {@link TripCancelRequest}.
     * </p>
     *
     * @param request The trip cancellation request containing the necessary
     *                details to cancel the trip.
     * @return An {@link ApiResponse} containing a message indicating the
     *         cancellation status.
     *
     * @see TripCancelRequest
     * @see ApiResponse
     */
    ApiResponse<String> cancel(TripCancelRequest request);

    /**
     * Cancels a trip quotation.
     * <p>
     * This method cancels an existing trip quotation based on the specified
     * {@link TripCancelRequest} and the quotation identifier.
     * </p>
     *
     * @param request The trip cancellation request containing the details to
     *                cancel the quotation.
     * @param quoteId The identifier of the quotation to be canceled.
     * @return An {@link ApiResponse} containing a message indicating the
     *         cancellation status.
     *
     * @see TripCancelRequest
     * @see ApiResponse
     */
    ApiResponse<String> cancel(TripCancelRequest request, Long quoteId);
}