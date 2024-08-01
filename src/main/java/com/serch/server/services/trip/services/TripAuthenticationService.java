package com.serch.server.services.trip.services;

import com.serch.server.models.trip.Trip;
import com.serch.server.models.trip.TripShare;
import com.serch.server.services.trip.requests.TripAuthRequest;

public interface TripAuthenticationService {
    /**
     * Create an authentication for the trip/shared trip
     *
     * @param trip The {@link Trip} data to link the authentication with
     * @param share The {@link TripShare} data to link the authentication with
     */
    void create(Trip trip, TripShare share);

    /**
     * Verify a trip authentication code
     *
     * @param request The {@link TripAuthRequest} data for authentication verification
     * @param code The code to check against
     *
     * @return true, if verified or false
     */
    Boolean verify(TripAuthRequest request, String code);
}
