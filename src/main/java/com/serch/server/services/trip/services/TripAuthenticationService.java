package com.serch.server.services.trip.services;

import com.serch.server.models.trip.Trip;
import com.serch.server.models.trip.TripShare;
import com.serch.server.services.trip.requests.TripAuthRequest;

/**
 * Service interface for handling trip authentication processes.
 * <p>
 * This interface provides methods for creating trip authentication links
 * and verifying trip authentication codes. It is used to manage the
 * security and authorization for shared trip access.
 * </p>
 */
public interface TripAuthenticationService {

    /**
     * Creates an authentication link for a trip or shared trip.
     * <p>
     * This method is used to set up authentication for a trip, enabling
     * secure access to the trip details or shared trip data. The
     * authentication is associated with both the given {@link Trip} and
     * {@link TripShare} instances.
     * </p>
     *
     * @param trip  The {@link Trip} instance containing details of the trip
     *              to link the authentication with.
     * @param share The {@link TripShare} instance containing details of the
     *              shared trip to link the authentication with.
     */
    void create(Trip trip, TripShare share);

    /**
     * Verifies a trip authentication code.
     * <p>
     * This method checks the provided authentication code against the data
     * contained in the {@link TripAuthRequest}. If the code matches the
     * expected value, the authentication is considered valid.
     * </p>
     *
     * @param request The {@link TripAuthRequest} containing data needed for
     *                authentication verification, such as trip details and user information.
     * @param code    The authentication code to be checked.
     * @return {@code true} if the authentication code is verified successfully,
     *         {@code false} otherwise.
     */
    Boolean verify(TripAuthRequest request, String code);
}