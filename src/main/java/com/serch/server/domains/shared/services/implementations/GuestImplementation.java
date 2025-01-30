package com.serch.server.domains.shared.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.core.session.GuestSessionService;
import com.serch.server.core.token.TokenService;
import com.serch.server.domains.account.services.ProfileService;
import com.serch.server.domains.auth.requests.RequestProfile;
import com.serch.server.domains.auth.responses.AuthResponse;
import com.serch.server.domains.auth.services.UserAuthService;
import com.serch.server.domains.shared.requests.GuestToUserRequest;
import com.serch.server.domains.shared.requests.SwitchRequest;
import com.serch.server.domains.shared.services.GuestService;
import com.serch.server.exceptions.others.SharedException;
import com.serch.server.mappers.SharedMapper;
import com.serch.server.models.account.Profile;
import com.serch.server.models.auth.User;
import com.serch.server.models.shared.Guest;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.repositories.shared.GuestRepository;
import com.serch.server.repositories.shared.SharedLinkRepository;
import com.serch.server.repositories.shared.SharedLoginRepository;
import com.serch.server.utils.AuthUtil;
import com.serch.server.utils.HelperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * This is the class that contains the logic and implementation of its wrapper class {@link GuestService}
 *
 * @see TokenService
 * @see ProfileService
 * @see UserAuthService
 * @see PasswordEncoder
 * @see GuestRepository
 * @see UserRepository
 * @see SharedLinkRepository
 */
@Service
@RequiredArgsConstructor
public class GuestImplementation implements GuestService {
    private final ProfileService profileService;
    private final GuestSessionService sessionService;
    private final UserAuthService userAuthService;
    private final SimpMessagingTemplate messaging;
    private final GuestRepository guestRepository;
    private final UserRepository userRepository;
    private final SharedLoginRepository sharedLoginRepository;
    private final AuthUtil authUtil;

    @Value("${application.security.otp-expiration-time}")
    protected Integer OTP_EXPIRATION_TIME;

    @Override
    public ApiResponse<AuthResponse> becomeAUser(GuestToUserRequest request) {
        Guest guest = authUtil.getGuest();

        if(guest.isEmailConfirmed()) {
            Optional<User> existingUser = userRepository.findByEmailAddressIgnoreCase(guest.getEmailAddress());

            if(existingUser.isPresent()) {
                throw new SharedException("User already exists");
            } else {
                if(HelperUtil.validatePassword(request.getPassword())) {
                    RequestProfile profile = SharedMapper.INSTANCE.profile(request);
                    profile.setFirstName(guest.getFirstName());
                    profile.setLastName(guest.getLastName());
                    profile.setFcmToken(guest.getFcmToken());
                    profile.setGender(guest.getGender());
                    User user = userAuthService.getNewUser(profile, guest.getConfirmedAt());

                    ApiResponse<Profile> response = profileService.createUserProfile(
                            profile,
                            user,
                            authUtil.getShared().getSharedLink().getUser().getUser()
                    );

                    if(response.getStatus().is2xxSuccessful()) {
                        return userAuthService.getAuthResponse(profile, user);
                    }else {
                        return new ApiResponse<>(response.getMessage());
                    }
                } else {
                    throw new SharedException("Password must contain a lowercase, uppercase, special character and number");
                }
            }
        } else {
            throw new SharedException("Email is not confirmed");
        }
    }

    @Override
    public void refresh(SwitchRequest request) {
        sharedLoginRepository.findBySharedLink_IdAndGuest_Id(request.getLinkId(), request.getId())
                .ifPresent(login -> messaging.convertAndSend(
                        "/platform/%s/%s".formatted(login.getGuest().getId(), login.getSharedLink().getId()),
                        sessionService.response(login)
                ));
    }

    @Override
    public ApiResponse<String> updateFcmToken(String token) {
        guestRepository.findById(authUtil.getGuestId()).ifPresent(profile -> {
            profile.setFcmToken(token);
            guestRepository.save(profile);
        });

        return new ApiResponse<>("Successfully updated FCM token", HttpStatus.OK);
    }

    @Override
    public ApiResponse<String> updateTimezone(String timezone) {
        guestRepository.findById(authUtil.getGuestId()).ifPresent(profile -> {
            profile.setTimezone(timezone);
            guestRepository.save(profile);
        });

        return new ApiResponse<>("Successfully updated timezone", HttpStatus.OK);
    }
}