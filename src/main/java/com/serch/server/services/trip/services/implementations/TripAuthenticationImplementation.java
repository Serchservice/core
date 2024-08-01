package com.serch.server.services.trip.services.implementations;

import com.serch.server.models.trip.Trip;
import com.serch.server.models.trip.TripAuthentication;
import com.serch.server.models.trip.TripShare;
import com.serch.server.repositories.trip.TripAuthenticationRepository;
import com.serch.server.services.auth.services.TokenService;
import com.serch.server.services.trip.requests.TripAuthRequest;
import com.serch.server.services.trip.services.TripAuthenticationService;
import com.serch.server.utils.DatabaseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TripAuthenticationImplementation implements TripAuthenticationService {
    private final TokenService tokenService;
    private final TripAuthenticationRepository tripAuthenticationRepository;

    @Override
    public void create(Trip trip, TripShare share) {
        TripAuthentication authentication = new TripAuthentication();
        if(share != null) {
            authentication.setSharing(share);
        } else {
            authentication.setTrip(trip);
        }

        authentication.setCode(DatabaseUtil.encodeData(tokenService.generateCode(4)));
        tripAuthenticationRepository.save(authentication);
    }

    @Override
    public Boolean verify(TripAuthRequest request, String code) {
        return DatabaseUtil.decodeData(code).equals(request.getCode());
    }
}
