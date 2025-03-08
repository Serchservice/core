package com.serch.server.domains.trip.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.models.account.Profile;
import com.serch.server.models.trip.Trip;
import com.serch.server.domains.trip.requests.*;
import com.serch.server.domains.trip.responses.ActiveResponse;
import com.serch.server.domains.trip.responses.TripResponse;

import java.util.List;

/**
 * TripService interface defines the operations related to trip management.
 * It provides methods for requesting, accepting, ending, canceling trips, and other related functionalities.
 */
public interface TripService {
    /**
     * Request a new trip.
     *
     * @param request the trip request details, encapsulated in a {@link TripInviteRequest}.
     *                This includes information about the user, destination, and any additional trip-related response.
     *
     * @return an {@link ApiResponse} containing a {@link TripResponse} which represents the details of the active trip.
     *         The response includes the status and response of the trip request.
     *
     * @see TripInviteRequest
     * @see ApiResponse
     * @see TripResponse
     */
    ApiResponse<TripResponse> request(TripInviteRequest request);

    /**
     * Recreate an existing trip, effectively rebooking it with the same or modified response.
     *
     * @param withInvited whether to use the previously invited provider as the main provider in the newly created trip.
     *                    If true, the invited provider from the original trip becomes the main provider.
     * @param id the unique identifier of the existing trip to be used for recreating the same trip response.
     *
     * @return an {@link ApiResponse} containing the newly created {@link TripResponse} response.
     *         The response provides details of the rebooked trip, including any changes made.
     */
    ApiResponse<TripResponse> rebook(String id, Boolean withInvited);

    /**
     * Create a new trip from a scheduled plan.
     *
     * @param request the {@link TripInviteRequest} containing the trip details such as destination, time, and participants.
     * @param account the {@link Profile} of the user who is requesting the trip.
     * @param profile the {@link Profile} of the provider who will be assigned to the trip.
     *
     * @return an {@link ApiResponse} containing the requested {@link TripResponse}.
     *         This provides details of the scheduled trip, including the status and provider information.
     */
    ApiResponse<TripResponse> request(TripInviteRequest request, Profile account, Profile profile);

    /**
     * Accept a trip request, confirming the trip and setting it as active.
     *
     * @param request the {@link TripAcceptRequest} containing the acceptance details.
     *                This may include confirmation information and any special conditions.
     *
     * @return an {@link ApiResponse} containing a {@link TripResponse} representing the active trip details.
     *         The response indicates if the acceptance was successful and provides the current status of the trip.
     *
     * @see TripAcceptRequest
     * @see ApiResponse
     * @see TripResponse
     */
    ApiResponse<TripResponse> accept(TripAcceptRequest request);

    /**
     * End an active trip, effectively marking it as completed.
     *
     * @param request the {@link TripCancelRequest} containing the trip ending details.
     *                This may include reasons for ending and additional metadata about the trip.
     *
     * @return an {@link ApiResponse} containing a list of {@link TripResponse} instances.
     *         The response provides the details of the ended trip and any associated trips that may have been affected.
     *
     * @see TripCancelRequest
     * @see ApiResponse
     */
    ApiResponse<List<TripResponse>> end(TripCancelRequest request);

    /**
     * Leave an active trip, removing a participant from the trip.
     *
     * @param id the unique identifier of the active trip that the participant wants to leave.
     *
     * @return an {@link ApiResponse} containing a list of {@link TripResponse} instances.
     *         This response indicates the updated state of the trip after the participant has left.
     *
     * @see ApiResponse
     * @see TripResponse
     */
    ApiResponse<List<TripResponse>> leave(String id);

    /**
     * Search for active providers based on their location and optional phone number.
     *
     * @param phoneNumber the phone number of the provider (optional). If provided, will be used to filter the search.
     * @param lat the latitude of the provider's location.
     * @param lng the longitude of the provider's location.
     * @param page The page number to retrieve (zero-based index).
     * @param size The number of items per page.
     *
     * @return an {@link ApiResponse} containing a list of {@link ActiveResponse} instances.
     *         The response provides details of providers who match the search criteria.
     *
     * @see ApiResponse
     * @see ActiveResponse
     */
    ApiResponse<List<ActiveResponse>> search(String phoneNumber, Double lat, Double lng, Integer page, Integer size);

    /**
     * Verify the authentication details of a trip.
     *
     * @param request the {@link TripAuthRequest} containing authentication details such as user credentials or tokens.
     *
     * @return an {@link ApiResponse} containing the active {@link TripResponse}.
     *         The response indicates whether the authentication was successful.
     *
     * @see TripAuthRequest
     * @see ApiResponse
     * @see TripResponse
     */
    ApiResponse<TripResponse> auth(TripAuthRequest request);

    /**
     * Cancel a trip, making it inactive.
     *
     * @param request the {@link TripCancelRequest} containing details of the trip to cancel.
     *
     * @return an {@link ApiResponse} containing a list of {@link TripResponse} instances.
     *         The response provides the status of the canceled trip and any related trips.
     *
     * @see TripCancelRequest
     * @see ApiResponse
     */
    ApiResponse<List<TripResponse>> cancel(TripCancelRequest request);

    /**
     * Attempt to pay the service fee for a trip, if it hasn't been paid yet.
     *
     * @param id the unique identifier of the trip for which the service fee is to be paid.
     *
     * @return an {@link ApiResponse} containing the {@link TripResponse} details after attempting the payment.
     *         The response indicates whether the payment was successful.
     */
    ApiResponse<TripResponse> payServiceFee(String id);

    /**
     * Verify a transaction for special trip dtos, ensuring the transaction reference is valid.
     *
     * @param id the unique identifier of the trip.
     * @param guest the unique identifier of the guest (optional). Can be used for guest verification.
     * @param reference the transaction reference used for the trip payment.
     *
     * @return an {@link ApiResponse} containing the {@link TripResponse} after verifying the transaction.
     *         The response provides details of the verification outcome.
     */
    ApiResponse<TripResponse> verify(String id, String guest, String reference);

    /**
     * Update the details of a trip, such as destination, status, or participant information.
     *
     * @param request the {@link TripUpdateRequest} containing the new details to be applied to the trip.
     *
     * @return an {@link ApiResponse} containing the updated {@link TripResponse}.
     *         The response indicates the updated state of the trip.
     *
     * @see TripUpdateRequest
     */
    ApiResponse<TripResponse> update(TripUpdateRequest request);

    /**
     * Update other related entities' response within the trip, such as linked bookings or notifications.
     *
     * @param trip the {@link Trip} instance containing the response to be updated.
     *             This can include changes to trip metadata or associated records.
     */
    void updateOthers(Trip trip);

    /**
     * Update the location details of a provider within the trip context.
     *
     * @param request the {@link MapViewRequest} containing the new location response.
     *                This may involve updating the provider's latitude, longitude, or other location-related information.
     *
     * @see MapViewRequest
     */
    void update(MapViewRequest request);
}