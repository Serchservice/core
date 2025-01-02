package com.serch.server.services.account.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.exceptions.account.AccountException;
import com.serch.server.models.auth.User;
import com.serch.server.models.shared.Guest;
import com.serch.server.repositories.account.BusinessProfileRepository;
import com.serch.server.repositories.account.ProfileRepository;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.repositories.conversation.CallRepository;
import com.serch.server.repositories.shared.GuestRepository;
import com.serch.server.repositories.trip.TripRepository;
import com.serch.server.services.account.requests.UpdateE2EKey;
import com.serch.server.services.account.responses.AccountResponse;
import com.serch.server.services.account.services.AccountService;
import com.serch.server.services.shared.services.GuestService;
import com.serch.server.services.shared.services.SharedService;
import com.serch.server.utils.DatabaseUtil;
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
    private final BusinessProfileRepository businessProfileRepository;
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

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
        String time = TimeUtil.formatDay(userUtil.getUser().getLastUpdatedAt(), userUtil.getUser().getTimezone());
        return new ApiResponse<>("Success", time, HttpStatus.OK);
    }

    @Override
    public ApiResponse<String> updateFcmToken(String token) {
        profileRepository.findById(userUtil.getUser().getId()).ifPresentOrElse(profile -> {
            profile.setFcmToken(token);
            profileRepository.save(profile);
            }, () -> businessProfileRepository.findById(userUtil.getUser().getId()).ifPresent(business -> {
                business.setFcmToken(token);
                businessProfileRepository.save(business);
            })
        );

        return new ApiResponse<>("Successfully updated FCM token", HttpStatus.OK);
    }

    @Override
    public ApiResponse<String> updateTimezone(String timezone) {
        profileRepository.findById(userUtil.getUser().getId()).ifPresentOrElse(profile -> {
                    profile.getUser().setTimezone(timezone);
                    userRepository.save(profile.getUser());
                }, () -> businessProfileRepository.findById(userUtil.getUser().getId()).ifPresent(business -> {
                    business.getUser().setTimezone(timezone);
                    userRepository.save(business.getUser());
                })
        );

        return new ApiResponse<>("Successfully updated timezone", HttpStatus.OK);
    }

    @Override
    public ApiResponse<String> updatePublicEncryptionKey(UpdateE2EKey key) {
        profileRepository.findById(userUtil.getUser().getId()).ifPresentOrElse(profile -> {
            profile.setPublicEncryptionKey(DatabaseUtil.encodeData(key.getPublicKey()));
            profileRepository.save(profile);
            }, () -> businessProfileRepository.findById(userUtil.getUser().getId()).ifPresent(business -> {
                business.setPublicEncryptionKey(DatabaseUtil.encodeData(key.getPublicKey()));
                businessProfileRepository.save(business);
            })
        );

        return new ApiResponse<>("Successfully updated public key", HttpStatus.OK);
    }
}