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
 * Service interface for managing trip history and related activities.
 * <p>
 * This interface provides methods for preparing trip responses, retrieving
 * invite history, and fetching the trip history of users or providers.
 * </p>
 *
 * @see TripHistoryImplementation
 * @see com.serch.server.services.trip.controllers.TripController
 */
public interface TripHistoryService {

    /**
     * Prepares the response for a trip invitation.
     * <p>
     * This method generates a {@link TripResponse} based on the specified trip
     * invitation ID and the requesting user ID.
     * </p>
     *
     * @param userId The ID of the user requesting the data.
     * @param id     The ID of the {@link com.serch.server.models.trip.TripInvite}
     *               associated with the trip invitation.
     * @return The {@link TripResponse} containing details of the invited trip.
     */
    TripResponse response(String id, String userId);

    /**
     * Prepares the response based on trip data.
     * <p>
     * This method generates a {@link TripResponse} for the specified trip,
     * incorporating optional payment data and socket update configurations.
     * </p>
     *
     * @param userId       The ID of the user requesting the data.
     * @param id           The ID of the {@link Trip}.
     * @param payment      The {@link InitializePaymentData} containing payment
     *                     information (can be {@code null}).
     * @param sendUpdate   Specifies whether to send a socket update to all accounts
     *                     associated with the trip.
     * @param requestedId  An optional ID representing the requested data (can be used
     *                     for further identification).
     * @return The {@link TripResponse} containing the trip's details.
     */
    TripResponse response(String id, String userId, @Nullable InitializePaymentData payment, boolean sendUpdate, String requestedId);

    /**
     * Retrieves a list of pending trip invitations for the specified user.
     * <p>
     * This method returns the trip invitation history, including shared and guest trips,
     * for the user associated with the specified ID.
     * </p>
     *
     * @param userId  The ID of the user whose trip invitation history is to be retrieved.
     * @param linkId  The shared link ID (can be {@code null}).
     * @param guestId The guest ID (can be {@code null}).
     * @return A list of {@link TripResponse} objects representing the pending trip invitations.
     */
    List<TripResponse> inviteHistory(String guestId, UUID userId, String linkId);

    /**
     * Retrieves the trip history for the specified trip.
     * <p>
     * This method fetches the history of trips the user or provider has participated in,
     * with optional configurations for sending socket updates and specifying shared link
     * and guest details.
     * </p>
     *
     * @param trip        The trip ID to fetch the history for.
     * @param sendUpdate  Indicates whether to send a socket update to all associated accounts.
     * @param linkId      The shared link ID (can be {@code null}).
     * @param guest       The guest ID (can be {@code null}).
     * @return An {@link ApiResponse} containing a list of {@link TripResponse} objects.
     */
    ApiResponse<List<TripResponse>> history(String guest, String linkId, boolean sendUpdate, String trip);

    /**
     * Retrieves the trip history for the logged-in user or provider.
     * <p>
     * This method returns the list of trips based on shared link and guest details,
     * representing the trips the user or provider has been a part of.
     * </p>
     *
     * @param linkId The shared link ID (can be {@code null}).
     * @param guest  The guest ID (can be {@code null}).
     * @return An {@link ApiResponse} containing a list of {@link TripResponse} objects.
     */
    ApiResponse<List<TripResponse>> history(String guest, String linkId);
}