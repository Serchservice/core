package com.serch.server.services.trip.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.trip.TripStatus;
import com.serch.server.services.trip.requests.OnlineRequest;

public interface ActiveService {
    ApiResponse<TripStatus> toggleStatus(OnlineRequest request);
    ApiResponse<TripStatus> fetchStatus();
}
