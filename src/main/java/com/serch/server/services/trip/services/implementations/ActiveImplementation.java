package com.serch.server.services.trip.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.trip.TripStatus;
import com.serch.server.models.trip.Active;
import com.serch.server.repositories.trip.ActiveRepository;
import com.serch.server.services.trip.requests.OnlineRequest;
import com.serch.server.services.trip.services.ActiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ActiveImplementation implements ActiveService {

    private final ActiveRepository activeRepository;

    @Override
    public ApiResponse<TripStatus> toggleStatus(OnlineRequest request) {
        Optional<Active> existing = activeRepository.find
        return null;
    }

    @Override
    public ApiResponse<TripStatus> fetchStatus() {
        return null;
    }
}
