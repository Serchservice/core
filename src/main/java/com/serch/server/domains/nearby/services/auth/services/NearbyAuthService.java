package com.serch.server.domains.nearby.services.auth.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.domains.nearby.services.auth.requests.NearbyDeviceRequest;

import java.util.UUID;

/**
 * Service interface for managing nearby device authentication and registration.
 */
public interface NearbyAuthService {

    /**
     * Registers a nearby device based on the provided request.
     *
     * @param request the request containing details of the device to be registered.
     * @return an {@link ApiResponse} containing the UUID of the registered device.
     */
    ApiResponse<UUID> register(NearbyDeviceRequest request);
}