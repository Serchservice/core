package com.serch.server.services.shared.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.exceptions.others.SharedException;
import com.serch.server.models.auth.User;
import com.serch.server.models.shared.Guest;
import com.serch.server.models.shared.SharedLink;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.repositories.shared.GuestRepository;
import com.serch.server.repositories.shared.SharedLinkRepository;
import com.serch.server.repositories.trip.TripRepository;
import com.serch.server.services.auth.requests.RequestProfile;
import com.serch.server.services.auth.responses.AuthResponse;
import com.serch.server.services.auth.services.UserAuthService;
import com.serch.server.services.shared.requests.SwitchRequest;
import com.serch.server.services.shared.responses.GuestResponse;
import com.serch.server.services.shared.services.GuestAuthService;
import com.serch.server.services.shared.services.SwitchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.serch.server.enums.trip.TripConnectionStatus.ACCEPTED;
import static com.serch.server.enums.trip.TripConnectionStatus.ON_TRIP;

/**
 * This is the class that contains the logic and implementation of its wrapper class.
 *
 * @see GuestAuthService
 * @see UserAuthService
 * @see PasswordEncoder
 * @see GuestRepository
 * @see UserRepository
 * @see TripRepository
 */
@Service
@RequiredArgsConstructor
public class SwitchImplementation implements SwitchService {
    private final GuestAuthService authService;
    private final UserAuthService userAuthService;
    private final PasswordEncoder passwordEncoder;
    private final GuestRepository guestRepository;
    private final UserRepository userRepository;
    private final TripRepository tripRepository;
    private final SharedLinkRepository sharedLinkRepository;

    @Override
    public ApiResponse<GuestResponse> switchToGuest(SwitchRequest request) {
        Guest guest = guestRepository.findByIdAndSharedLinks_Id(request.getTo(), request.getLinkId())
                .orElseThrow(() -> new SharedException("Couldn't find the account you want to switch to"));
        SharedLink sharedLink = sharedLinkRepository.findById(request.getLinkId())
                .orElseThrow(() -> new SharedException("Link not found"));

        if(guest.getSharedLinks().stream().anyMatch(link -> link.getId().equals(request.getLinkId()))) {
            checkRequest(request);
            authService.checkLink(sharedLink, guest.getId());
            return new ApiResponse<>(
                    "Can switch account",
                    authService.response(
                            guest.getSharedLinks()
                                    .stream()
                                    .filter(link -> link.getId().equals(request.getLinkId()))
                                    .toList()
                                    .get(0),
                            guest
                    ),
                    HttpStatus.OK
            );
        } else {
            throw new SharedException("Guest is not attached to link");
        }
    }

    @Override
    public ApiResponse<AuthResponse> switchToUser(SwitchRequest request) {
        try {
            UUID uuid = UUID.fromString(request.getTo());
            User user = userRepository.findById(uuid)
                    .orElseThrow(() -> new SharedException("Couldn't find the account you want to switch to"));

            if(passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                checkRequest(request);
                RequestProfile profile = new RequestProfile();
                profile.setDevice(request.getDevice());
                return userAuthService.getAuthResponse(profile, user);
            } else {
                throw new SharedException("Incorrect password details");
            }
        } catch (Exception e) {
            throw new SharedException(e.getMessage());
        }
    }

    private void checkRequest(SwitchRequest request) {
        String id = guestRepository.findById(request.getActive())
                .orElseThrow(() -> new SharedException("Guest not found"))
                .getId();

        if(tripRepository.existsByStatusAndAccount(ACCEPTED, ON_TRIP, id)) {
            throw new SharedException("Can't switch account when you are on a trip");
        }
    }
}
