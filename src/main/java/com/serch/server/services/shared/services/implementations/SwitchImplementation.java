package com.serch.server.services.shared.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.call.CallStatus;
import com.serch.server.enums.trip.TripConnectionStatus;
import com.serch.server.exceptions.others.SharedException;
import com.serch.server.models.auth.User;
import com.serch.server.models.conversation.Call;
import com.serch.server.models.shared.Guest;
import com.serch.server.models.trip.Trip;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.repositories.call.CallRepository;
import com.serch.server.repositories.shared.GuestRepository;
import com.serch.server.repositories.trip.TripRepository;
import com.serch.server.services.auth.requests.RequestProfile;
import com.serch.server.services.auth.responses.AuthResponse;
import com.serch.server.services.auth.services.UserAuthService;
import com.serch.server.services.shared.requests.SwitchRequest;
import com.serch.server.services.shared.responses.GuestResponse;
import com.serch.server.services.shared.services.GuestAuthService;
import com.serch.server.services.shared.services.SwitchService;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * This is the class that contains the logic and implementation of its wrapper class.
 *
 * @see GuestAuthService
 * @see UserAuthService
 * @see PasswordEncoder
 * @see GuestRepository
 * @see UserRepository
 * @see TripRepository
 * @see CallRepository
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
    private final CallRepository callRepository;

    @Override
    public ApiResponse<GuestResponse> switchToGuest(SwitchRequest request) {
        Guest guest = guestRepository.findById(request.getId())
                .orElseThrow(() -> new SharedException("Couldn't find the account you want to switch to"));

        if(guest.getSharedLinks().stream().anyMatch(link -> link.getId().equals(request.getLinkId()))) {
            checkRequest(request);
            return new ApiResponse<>(
                    "Can switch account",
                    authService.response(
                            guest.getSharedLinks()
                                    .stream()
                                    .filter(link -> link.getId().equals(request.getLinkId()))
                                    .toList()
                                    .getFirst(),
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
            UUID uuid = UUID.fromString(request.getId());
            User user = userRepository.findById(uuid)
                    .orElseThrow(() -> new SharedException("Couldn't find the account you want to switch to"));

            if(passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                checkRequest(request);
                RequestProfile profile = new RequestProfile();
                profile.setDevice(request.getDevice());
                profile.setPlatform(request.getPlatform());
                return userAuthService.getAuthResponse(profile, user);
            } else {
                throw new SharedException("Incorrect password details");
            }
        } catch (Exception e) {
            throw new SharedException(e.getMessage());
        }
    }

    private void checkRequest(SwitchRequest request) {
        String emailAddress;
        Optional<User> activeUser = userRepository.findByEmailAddressIgnoreCase(UserUtil.getLoginUser());

        if(activeUser.isPresent()) {
            emailAddress = activeUser.get().getEmailAddress();
        } else {
            emailAddress = guestRepository.findById(request.getActive())
                    .orElseThrow(() -> new SharedException("Guest not found"))
                    .getEmailAddress();
        }

        List<Trip> trips = tripRepository.findByEmailAddress(emailAddress);
        if(trips.stream().anyMatch(trip ->
                trip.getStatus() == TripConnectionStatus.ACCEPTED || trip.getStatus() == TripConnectionStatus.ON_TRIP
        )) {
            throw new SharedException("Can't switch account when you are on a trip");
        } else {
            if(activeUser.isPresent()) {
                List<Call> calls = callRepository.findBySerchId(activeUser.get().getId());
                if(calls.stream().anyMatch(call ->
                        call.getStatus() == CallStatus.ON_CALL || call.getStatus() == CallStatus.RINGING
                                || call.getStatus() == CallStatus.CALLING
                )) {
                    throw new SharedException("Can't switch account when you are on a call");
                }
            }
        }
    }
}
