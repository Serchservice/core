package com.serch.server.services.shared.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.exceptions.ExceptionCodes;
import com.serch.server.exceptions.others.SharedException;
import com.serch.server.mappers.SharedMapper;
import com.serch.server.models.account.Profile;
import com.serch.server.models.auth.User;
import com.serch.server.models.shared.Guest;
import com.serch.server.models.shared.GuestAuth;
import com.serch.server.models.shared.SharedLink;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.repositories.shared.GuestAuthRepository;
import com.serch.server.repositories.shared.GuestRepository;
import com.serch.server.repositories.shared.SharedLinkRepository;
import com.serch.server.services.account.services.ProfileService;
import com.serch.server.services.auth.requests.RequestProfile;
import com.serch.server.services.auth.responses.AuthResponse;
import com.serch.server.services.auth.services.TokenService;
import com.serch.server.services.auth.services.UserAuthService;
import com.serch.server.services.shared.requests.GuestToUserRequest;
import com.serch.server.services.shared.responses.GuestActivityResponse;
import com.serch.server.services.shared.services.GuestService;
import com.serch.server.utils.HelperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GuestImplementation implements GuestService {
    private final TokenService tokenService;
    private final ProfileService profileService;
    private final UserAuthService userAuthService;
    private final PasswordEncoder passwordEncoder;
    private final GuestRepository guestRepository;
    private final GuestAuthRepository guestAuthRepository;
    private final UserRepository userRepository;
    private final SharedLinkRepository sharedLinkRepository;

    @Value("${application.security.otp-expiration-time}")
    protected Integer OTP_EXPIRATION_TIME;

    @Override
    public ApiResponse<GuestActivityResponse> activity(String guestId, String linkId) {
        return null;
    }

    @Override
    public ApiResponse<String> checkIfEmailIsConfirmed(String guestId) {
        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new SharedException("Guest not found"));
        Optional<GuestAuth> auth = guestAuthRepository.findByEmailAddressIgnoreCase(guest.getEmailAddress());
        if(auth.isPresent()) {
            if(auth.get().isEmailConfirmed()) {
                return new ApiResponse<>("Email is confirmed", HttpStatus.OK);
            } else {
                String otp = tokenService.generateOtp();
                auth.get().setToken(passwordEncoder.encode(otp));
                auth.get().setExpiredAt(LocalDateTime.now().plusMinutes(OTP_EXPIRATION_TIME));
                guestAuthRepository.save(auth.get());
                throw new SharedException("An OTP was sent to your email address", ExceptionCodes.EMAIL_NOT_VERIFIED);
            }
        } else {
            GuestAuth guestAuth = new GuestAuth();
            String otp = tokenService.generateOtp();
            guestAuth.setToken(passwordEncoder.encode(otp));
            guestAuth.setExpiredAt(LocalDateTime.now().plusMinutes(OTP_EXPIRATION_TIME));
            guestAuth.setEmailAddress(guest.getEmailAddress());
            guestAuthRepository.save(guestAuth);
            throw new SharedException("An OTP was sent to your email address", ExceptionCodes.EMAIL_NOT_VERIFIED);
        }
    }

    @Override
    public ApiResponse<AuthResponse> becomeAUser(GuestToUserRequest request) {
        Guest guest = guestRepository.findById(request.getGuestId())
                .orElseThrow(() -> new SharedException("Guest not found"));
        GuestAuth auth = guestAuthRepository.findByEmailAddressIgnoreCase(guest.getEmailAddress())
                .orElseThrow(() -> new SharedException("Email confirmation not found"));
        SharedLink link = sharedLinkRepository.findByGuests_Id(guest.getId())
                .getFirst();

        if(auth.isEmailConfirmed()) {
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
                    User user = userAuthService.getNewUser(profile, auth.getConfirmedAt());
                    ApiResponse<Profile> response = profileService.createUserProfile(profile, user, link.getUser().getUser());

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
}
