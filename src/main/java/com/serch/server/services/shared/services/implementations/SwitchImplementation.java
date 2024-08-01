package com.serch.server.services.shared.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.call.CallStatus;
import com.serch.server.exceptions.ExceptionCodes;
import com.serch.server.exceptions.others.SharedException;
import com.serch.server.models.auth.User;
import com.serch.server.models.conversation.Call;
import com.serch.server.models.shared.SharedLink;
import com.serch.server.models.shared.SharedLogin;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.repositories.conversation.CallRepository;
import com.serch.server.repositories.shared.GuestRepository;
import com.serch.server.repositories.shared.SharedLinkRepository;
import com.serch.server.repositories.shared.SharedLoginRepository;
import com.serch.server.repositories.trip.TripRepository;
import com.serch.server.services.auth.requests.RequestProfile;
import com.serch.server.services.auth.responses.AuthResponse;
import com.serch.server.services.auth.services.UserAuthService;
import com.serch.server.services.shared.requests.SwitchRequest;
import com.serch.server.services.shared.responses.GuestResponse;
import com.serch.server.services.shared.services.GuestAuthService;
import com.serch.server.services.shared.services.GuestService;
import com.serch.server.services.shared.services.SwitchService;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * This is the class that contains the logic and implementation of its wrapper class.
 *
 * @see GuestAuthService
 * @see UserAuthService
 * @see GuestRepository
 * @see UserRepository
 * @see TripRepository
 */
@Service
@RequiredArgsConstructor
public class SwitchImplementation implements SwitchService {
    private final GuestService guestService;
    private final UserAuthService userAuthService;
    private final GuestRepository guestRepository;
    private final UserRepository userRepository;
    private final TripRepository tripRepository;
    private final SharedLoginRepository sharedLoginRepository;
    private final CallRepository callRepository;
    private final SharedLinkRepository sharedLinkRepository;

    @Override
    public ApiResponse<GuestResponse> switchToGuest(SwitchRequest request) {
        SharedLink sharedLink = sharedLinkRepository.findById(request.getLinkId())
                .orElseThrow(() -> new SharedException("Link not found"));

        if(sharedLink.cannotLogin()) {
            throw new SharedException("This link has run out of its usage, so you cannot access it.");
        } else {
            SharedLogin login = sharedLoginRepository.findBySharedLink_IdAndGuest_Id(request.getLinkId(), request.getId())
                    .orElseThrow(() -> new SharedException("Couldn't find the account you want to switch to"));
            Optional<User> user = userRepository.findByEmailAddressIgnoreCase(UserUtil.getLoginUser());
            if(user.isPresent()) {
                if(tripRepository.existsByStatusAndAccount(String.valueOf(user.get().getId()))) {
                    throw new SharedException("Can't switch account when you are on a trip");
                } else {
                    List<Call> calls = callRepository.findByUserId(user.get().getId());
                    if(calls.stream().anyMatch(call ->
                            call.getStatus() == CallStatus.ON_CALL || call.getStatus() == CallStatus.RINGING
                                    || call.getStatus() == CallStatus.CALLING
                    )) {
                        throw new SharedException("Can't switch account when you are on a call");
                    }
                }
            } else {
                checkRequest(request);
            }
            return new ApiResponse<>(guestService.response(login));
        }
    }

    @Override
    public ApiResponse<AuthResponse> switchToUser(SwitchRequest request) {
        User user = userRepository.findByEmailAddressIgnoreCase(UserUtil.getLoginUser())
                .orElseThrow(() -> new SharedException("Sign in as user to be able to switch easily"));

        checkRequest(request);
        RequestProfile profile = new RequestProfile();
        profile.setDevice(request.getDevice());
        return userAuthService.getAuthResponse(profile, user);
    }

    private void checkRequest(SwitchRequest request) {
        String id = guestRepository.findById(request.getId())
                .orElseThrow(() -> new SharedException("Guest not found"))
                .getId();

        if(tripRepository.existsByStatusAndAccount(id)) {
            throw new SharedException("Cannot switch account when you are on a trip", ExceptionCodes.ON_TRIP);
        }
    }
}
