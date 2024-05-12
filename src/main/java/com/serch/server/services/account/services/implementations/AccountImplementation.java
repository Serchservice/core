package com.serch.server.services.account.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.exceptions.account.AccountException;
import com.serch.server.models.auth.User;
import com.serch.server.models.shared.Guest;
import com.serch.server.repositories.account.ProfileRepository;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.repositories.conversation.CallRepository;
import com.serch.server.repositories.shared.GuestRepository;
import com.serch.server.repositories.trip.TripRepository;
import com.serch.server.services.account.responses.AccountResponse;
import com.serch.server.services.account.services.AccountService;
import com.serch.server.services.shared.services.GuestService;
import com.serch.server.services.shared.services.SharedService;
import com.serch.server.utils.TimeUtil;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * This is the class that contains the logic and implementation of its wrapper class {@link AccountService}.
 *
 * @see GuestService
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
    private final SharedService sharedService;
    private final GuestRepository guestRepository;

    @Override
    public ApiResponse<List<AccountResponse>> accounts() {
        Guest guest = guestRepository.findByEmailAddressIgnoreCase(UserUtil.getLoginUser())
                .orElse(null);
        User user = userUtil.getUser();

        if(user.isProfile()) {
            return sharedService.buildAccountResponse(guest, user);
        } else {
            throw new AccountException("Access denied. Your account cannot perform this action.");
        }
    }

    @Override
    public ApiResponse<String> lastPasswordUpdateAt() {
        String time = TimeUtil.formatDay(userUtil.getUser().getLastUpdatedAt());
        return new ApiResponse<>("Success", time, HttpStatus.OK);
    }
}