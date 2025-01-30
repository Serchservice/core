package com.serch.server.domains.trip.services.implementations;

import com.serch.server.core.sms.implementation.SmsService;
import com.serch.server.models.trip.Trip;
import com.serch.server.models.trip.TripAuthentication;
import com.serch.server.models.trip.TripShare;
import com.serch.server.repositories.trip.TripAuthenticationRepository;
import com.serch.server.core.token.TokenService;
import com.serch.server.domains.trip.requests.TripAuthRequest;
import com.serch.server.domains.trip.services.TripAuthenticationService;
import com.serch.server.utils.DatabaseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TripAuthenticationImplementation implements TripAuthenticationService {
    private final TokenService tokenService;
    private final SmsService smsService;
    private final TripAuthenticationRepository tripAuthenticationRepository;

    @Override
    @Transactional
    public void create(Trip trip, TripShare share) {
        TripAuthentication authentication = new TripAuthentication();
        if(share != null) {
            authentication.setSharing(share);
        } else {
            authentication.setTrip(trip);
        }

        String code = tokenService.generateCode(4);
        authentication.setCode(DatabaseUtil.encode(code));
        tripAuthenticationRepository.save(authentication);

        if(share != null && share.isOffline()) {
            String text = String.format("Hello %s %s", share.getFirstName(), share.getLastName()) +
                    "\n\n" +
                    String.format("You have been invited to a Serch shared trip by %s.", share.getProvider().getFullName()) +
                    "Call out the authentication code below when you arrive." +
                    "\n\n" +
                    String.format("Location: %s", share.getMapView().getPlace()) +
                    "\n\n" +
                    String.format("Click to open: https://www.google.com/maps?q=%s,%s", share.getMapView().getLatitude(), share.getMapView().getLongitude()) +
                    "\n\n" +
                    String.format("Authentication Code: %s", code);

            smsService.sendTripAuth(share.getPhoneNumber(), text);
        }
    }

    @Override
    public Boolean verify(TripAuthRequest request, String code) {
        return DatabaseUtil.decodeData(code).equals(request.getCode());
    }
}
