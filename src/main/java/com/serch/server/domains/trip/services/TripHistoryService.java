package com.serch.server.domains.trip.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.core.payment.responses.InitializePaymentData;
import com.serch.server.domains.trip.responses.TripResponse;
import com.serch.server.domains.trip.services.implementations.TripHistoryImplementation;
import com.serch.server.models.trip.Trip;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.List;

/**
 * Service interface for managing trip history and related activities.
 * <p>
 * This interface provides methods for preparing trip responses, retrieving
 * invite history, and fetching the trip history of users or providers.
 * </p>
 *
 * @see TripHistoryImplementation
 * @see com.serch.server.domains.trip.controllers.TripController
 */
public interface TripHistoryService {
    /**
     * Prepares the response for a trip invitation.
     * <p>
     * This method generates a {@link TripResponse} based on the specified trip
     * invitation ID and the requesting user ID.
     * </p>
     *
     * @param userId The ID of the user requesting the response.
     * @param id     The ID of the {@link com.serch.server.models.trip.TripInvite}
     *               associated with the trip invitation.
     * @return The {@link TripResponse} containing details of the invited trip.
     */
    TripResponse response(String id, String userId);

    /**
     * Prepares the response based on trip response.
     * <p>
     * This method generates a {@link TripResponse} for the specified trip,
     * incorporating optional payment response and socket update configurations.
     * </p>
     *
     * @param userId       The ID of the user requesting the response.
     * @param id           The ID of the {@link Trip}.
     * @param payment      The {@link InitializePaymentData} containing payment
     *                     information (can be {@code null}).
     * @param sendUpdate   Specifies whether to send a socket update to all accounts
     *                     associated with the trip.
     * @param requestedId  An optional ID representing the requested response (can be used
     *                     for further identification).
     * @return The {@link TripResponse} containing the trip's details.
     */
    TripResponse response(String id, String userId, @Nullable InitializePaymentData payment, boolean sendUpdate, String requestedId);

    /**
     * Retrieves the trip history for the logged-in user or provider.
     * <p>
     * This method returns the list of trips based on shared link and id details,
     * representing the trips the user or provider has been a part of.
     * </p>
     *
     * @param tripId   The trip ID to fetch the history for.
     * @param sendUpdate   Specifies whether to send a socket update to all accounts associated with the trip.
     * @param linkId The shared link ID (can be {@code null}).
     * @param id  The user/guest id (can be {@code null}).
     * @param page The page number to retrieve (zero-based index).
     * @param size The number of items per page.
     * @return An {@link ApiResponse} containing a list of {@link TripResponse} objects.
     */
    ApiResponse<List<TripResponse>> history(String id, String linkId, Integer page, Integer size, boolean sendUpdate, String tripId);

    /**
     * Retrieves the active trip history for the logged-in user or provider.
     * <p>
     * This method returns the list of trips based on shared link and id details,
     * representing the trips the user or provider has been a part of.
     * </p>
     *
     * @param tripId   The trip ID to fetch the history for.
     * @param sendUpdate   Specifies whether to send a socket update to all accounts associated with the trip.
     * @param linkId The shared link ID (can be {@code null}).
     * @param id  The user/guest id (can be {@code null}).
     * @param page The page number to retrieve (zero-based index).
     * @param size The number of items per page.
     * @return An {@link ApiResponse} containing a list of {@link TripResponse} objects.
     */
    ApiResponse<List<TripResponse>> active(String id, String linkId, Integer page, Integer size, boolean sendUpdate, String tripId);

    /**
     * Retrieves the requested trip history for the logged-in user or provider.
     * <p>
     * This method returns the list of trips based on shared link and id details,
     * representing the trips the user or provider has been a part of.
     * </p>
     *
     * @param tripId   The trip ID to fetch the history for.
     * @param sendUpdate   Specifies whether to send a socket update to all accounts associated with the trip.
     * @param linkId The shared link ID (can be {@code null}).
     * @param id  The user/guest id (can be {@code null}).
     * @param page The page number to retrieve (zero-based index).
     * @param size The number of items per page.
     * @return An {@link ApiResponse} containing a list of {@link TripResponse} objects.
     */
    ApiResponse<List<TripResponse>> requested(String id, String linkId, Integer page, Integer size, boolean sendUpdate, String tripId);

    /**
     * Retrieves the shared trip history for the logged-in user or provider.
     * <p>
     * This method returns the list of trips based on shared link and id details,
     * representing the trips the user or provider has been a part of.
     * </p>
     *
     * @param tripId   The trip ID to fetch the history for.
     * @param sendUpdate   Specifies whether to send a socket update to all accounts associated with the trip.
     * @param linkId The shared link ID (can be {@code null}).
     * @param id  The user/guest id (can be {@code null}).
     * @param page The page number to retrieve (zero-based index).
     * @param size The number of items per page.
     * @return An {@link ApiResponse} containing a list of {@link TripResponse} objects.
     */
    ApiResponse<List<TripResponse>> shared(String id, String linkId, Integer page, Integer size, boolean sendUpdate, String tripId);

    /**
     * Retrieves the trip history for the logged-in user or provider.
     * <p>
     * This method returns the list of trips based on shared link and id details,
     * representing the trips the user or provider has been a part of.
     * </p>
     *
     * @param dateTime   The date to fetch its history.
     * @param category   The category to fetch the history for.
     * @param isShared   Specifies whether to fetch shared trips.
     * @param linkId The shared link ID (can be {@code null}).
     * @param id  The user/guest id (can be {@code null}).
     * @param page The page number to retrieve (zero-based index).
     * @param size The number of items per page.
     * @return An {@link ApiResponse} containing a list of {@link TripResponse} objects.
     */
    ApiResponse<List<TripResponse>> history(String id, String linkId, Integer page, Integer size, Boolean isShared, String category, Date dateTime);
}