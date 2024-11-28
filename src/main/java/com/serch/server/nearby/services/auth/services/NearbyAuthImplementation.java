package com.serch.server.nearby.services.auth.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.exceptions.nearby.NearbyException;
import com.serch.server.nearby.mappers.NearbyMapper;
import com.serch.server.nearby.models.Nearby;
import com.serch.server.nearby.repositories.NearbyRepository;
import com.serch.server.nearby.services.auth.requests.NearbyDeviceRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NearbyAuthImplementation implements NearbyAuthService {
    private final NearbyRepository nearbyRepository;

    @Override
    public ApiResponse<UUID> register(NearbyDeviceRequest request) {
        if(request.getFcmToken() == null || request.getFcmToken().isEmpty()) {
            throw new NearbyException("Messaging token is empty");
        }

        Optional<Nearby> optional = nearbyRepository.findByFcmTokenIgnoreCaseOrId(request.getFcmToken(), request.getId());
        if(optional.isPresent()) {
            Nearby nearby = optional.get();
            NearbyMapper.instance.updateNearbyFromDto(request, nearby);
            nearbyRepository.save(nearby);

            return new ApiResponse<>(nearby.getId());
        } else {
            Nearby data = NearbyMapper.instance.nearby(request);
            Nearby nearby = nearbyRepository.save(data);

            return new ApiResponse<>(nearby.getId());
        }
    }
}
