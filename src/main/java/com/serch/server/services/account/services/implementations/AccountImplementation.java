package com.serch.server.services.account.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.call.CallStatus;
import com.serch.server.exceptions.account.AccountException;
import com.serch.server.exceptions.others.SharedException;
import com.serch.server.models.account.Profile;
import com.serch.server.models.auth.User;
import com.serch.server.models.conversation.Call;
import com.serch.server.models.shared.Guest;
import com.serch.server.models.shared.SharedLink;
import com.serch.server.repositories.account.ProfileRepository;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.repositories.call.CallRepository;
import com.serch.server.repositories.shared.GuestRepository;
import com.serch.server.repositories.trip.TripRepository;
import com.serch.server.services.account.responses.AccountResponse;
import com.serch.server.services.account.services.AccountService;
import com.serch.server.services.shared.requests.SwitchRequest;
import com.serch.server.services.shared.responses.GuestResponse;
import com.serch.server.services.shared.responses.SharedAccountResponse;
import com.serch.server.services.shared.services.GuestAuthService;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

import static com.serch.server.enums.trip.TripConnectionStatus.ACCEPTED;
import static com.serch.server.enums.trip.TripConnectionStatus.ON_TRIP;

/**
 * This is the class that contains the logic and implementation of its wrapper class {@link AccountService}.
 *
 * @see GuestAuthService
 * @see CallRepository
 * @see ProfileRepository
 * @see PasswordEncoder
 * @see GuestRepository
 * @see UserRepository
 * @see TripRepository
 */
@Service
@RequiredArgsConstructor
public class AccountImplementation implements AccountService {
    private final UserUtil userUtil;
    private final GuestAuthService guestAuthService;
    private final GuestRepository guestRepository;
    private final UserRepository userRepository;
    private final TripRepository tripRepository;
    private final CallRepository callRepository;
    private final ProfileRepository profileRepository;

    @Override
    public ApiResponse<AccountResponse> accounts() {
        Guest guest = guestRepository.findByEmailAddressIgnoreCase(UserUtil.getLoginUser())
                .orElse(new Guest());
        User user = userUtil.getUser();

        if(user.isProfile()) {
            return buildAccountResponse(guest, user);
        } else {
            throw new AccountException("Access denied. Your account cannot perform this action.");
        }
    }

    @Override
    public ApiResponse<GuestResponse> switchToGuest(SwitchRequest request) {
        Guest guest = guestRepository.findById(request.getTo())
                .orElseThrow(() -> new SharedException("Couldn't find the account you want to switch to"));

        if(guest.getSharedLinks().stream().anyMatch(link -> link.getId().equals(request.getLinkId()))) {
            User user = userRepository.findByEmailAddressIgnoreCase(UserUtil.getLoginUser())
                            .orElseThrow(() -> new AccountException("User not found"));

            if(tripRepository.existsByStatusAndAccount(ACCEPTED, ON_TRIP, String.valueOf(user.getId()))) {
                throw new SharedException("Can't switch account when you are on a trip");
            } else {
                List<Call> calls = callRepository.findByUserId(user.getId());
                if(calls.stream().anyMatch(call ->
                        call.getStatus() == CallStatus.ON_CALL || call.getStatus() == CallStatus.RINGING
                                || call.getStatus() == CallStatus.CALLING
                )) {
                    throw new SharedException("Can't switch account when you are on a call");
                }
            }

            return new ApiResponse<>(
                    "Can switch account",
                    guestAuthService.response(
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
    public ApiResponse<AccountResponse> buildAccountResponse(Guest guest, User user) {
        List<SharedAccountResponse> list;
        list = guest.getSharedLinks()
                .stream()
                .sorted(Comparator.comparing(SharedLink::getCreatedAt))
                .map(link -> {
                    SharedAccountResponse response = new SharedAccountResponse();
                    response.setId(guest.getId());
                    response.setAvatar(guest.getAvatar());
                    response.setCount(link.getStatuses().size());
                    response.setLink(link.getLink());
                    response.setName(guest.getFullName());
                    response.setMode("GUEST");
                    response.setEmailAddress(guest.getEmailAddress());
                    response.setLinkId(link.getId());
                    return response;
                })
                .toList();

        AccountResponse response = new AccountResponse();
        response.setAvatar(
                profileRepository.findById(user.getId())
                        .map(Profile::getAvatar)
                        .orElse("")
        );
        response.setName(user.getFullName());
        response.setMode("USER");
        response.setId(user.getId().toString());
        response.setGuests(list);
        response.setEmailAddress(user.getEmailAddress());
        return new ApiResponse<>(response);
    }
}
