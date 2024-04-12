package com.serch.server.services.auth.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.auth.AuthMethod;
import com.serch.server.enums.auth.Role;
import com.serch.server.exceptions.ExceptionCodes;
import com.serch.server.exceptions.auth.AuthException;
import com.serch.server.mappers.AuthMapper;
import com.serch.server.models.account.Profile;
import com.serch.server.models.auth.User;
import com.serch.server.models.auth.incomplete.Incomplete;
import com.serch.server.models.auth.incomplete.IncompleteProfile;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.repositories.auth.incomplete.IncompleteRepository;
import com.serch.server.services.account.services.ProfileService;
import com.serch.server.services.account.services.ReferralService;
import com.serch.server.services.auth.requests.RequestLogin;
import com.serch.server.services.auth.requests.RequestProfile;
import com.serch.server.services.auth.requests.RequestSession;
import com.serch.server.services.auth.responses.AuthResponse;
import com.serch.server.services.auth.services.AuthService;
import com.serch.server.services.auth.services.SessionService;
import com.serch.server.services.auth.services.UserAuthService;
import com.serch.server.utils.HelperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserAuthImplementation implements UserAuthService {
    private final ProfileService profileService;
    private final ReferralService referralService;
    private final SessionService sessionService;
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final IncompleteRepository incompleteRepository;

    @Override
    public ApiResponse<AuthResponse> login(RequestLogin request) {
        var user = userRepository.findByEmailAddressIgnoreCase(request.getEmailAddress())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if(user.getRole() == Role.USER) {
            return authService.authenticate(request, user);
        } else {
            throw new AuthException("This email does not belong to a Serch User", ExceptionCodes.ACCESS_DENIED);
        }
    }

    @Override
    public ApiResponse<AuthResponse> signup(RequestProfile request) {
        var user = userRepository.findByEmailAddressIgnoreCase(request.getEmailAddress());
        if(user.isPresent()) {
            throw new AuthException("User already exists", ExceptionCodes.EXISTING_USER);
        } else {
            var incomplete = incompleteRepository.findByEmailAddress(request.getEmailAddress());
            if(incomplete.isPresent()) {
                if(!incomplete.get().isEmailConfirmed()) {
                    authService.sendOtp(request.getEmailAddress());
                    throw new AuthException("Email not verified", ExceptionCodes.EMAIL_NOT_VERIFIED);
                } else {
                    return getSignupAuthResponse(request, incomplete.get());
                }
            } else {
                authService.sendOtp(request.getEmailAddress());
                throw new AuthException("User not found. Check your email for verification", ExceptionCodes.USER_NOT_FOUND);
            }
        }
    }

    private ApiResponse<AuthResponse> getSignupAuthResponse(RequestProfile request, Incomplete incomplete) {
        User referral = null;
        if(request.getReferral() != null && !request.getReferral().isEmpty()) {
            referral = referralService.verifyReferralCode(request.getReferral());
        }

        if(HelperUtil.validatePassword(request.getPassword())) {
            incompleteRepository.delete(incomplete);

            User newUser = getNewUser(request, incomplete.getTokenConfirmedAt());
            ApiResponse<Profile> response = profileService.createUserProfile(request, newUser, referral);
            if(response.getStatus().is2xxSuccessful()) {
                return getAuthResponse(request, newUser);
            } else {
                return new ApiResponse<>(response.getMessage());
            }
        } else {
            throw new AuthException(
                    "Password must contain a lowercase, uppercase, special character and number",
                    ExceptionCodes.INCORRECT_TOKEN
            );
        }
    }

    @Override
    public ApiResponse<AuthResponse> getAuthResponse(RequestProfile request, User newUser) {
        RequestSession requestSession = new RequestSession();
        requestSession.setPlatform(request.getPlatform());
        requestSession.setMethod(AuthMethod.PASSWORD);
        requestSession.setUser(newUser);
        requestSession.setDevice(request.getDevice());
        var session = sessionService.generateSession(requestSession);

        return new ApiResponse<>(AuthResponse.builder()
                .mfaEnabled(newUser.getMfaEnabled())
                .session(session.getData())
                .firstName(newUser.getFirstName())
                .recoveryCodesEnabled(newUser.getRecoveryCodeEnabled())
                .build()
        );
    }

    @Override
    public User getNewUser(RequestProfile profile, LocalDateTime confirmedAt) {
        User user = new User();
        user.setEmailAddress(profile.getEmailAddress());
        user.setPassword(passwordEncoder.encode(profile.getPassword()));
        user.setEmailConfirmedAt(confirmedAt);
        user.setRole(Role.USER);
        user.setFirstName(profile.getFirstName());
        user.setLastName(profile.getLastName());
        return userRepository.save(user);
    }

    @Override
    public ApiResponse<AuthResponse> becomeAUser(RequestLogin login) {
        var incomplete = incompleteRepository.findByEmailAddress(login.getEmailAddress())
                .orElseThrow(() -> new AuthException("User does not exist", ExceptionCodes.USER_NOT_FOUND));
        if(!incomplete.isEmailConfirmed()) {
            authService.sendOtp(login.getEmailAddress());
            throw new AuthException("Email is not verified", ExceptionCodes.EMAIL_NOT_VERIFIED);
        } else {
            var profileInfo = incomplete.getProfile();
            var phoneInfo = incomplete.getPhoneInfo();
            if(profileInfo == null || phoneInfo == null) {
                throw new AuthException("You have no profile. Signup to create one", ExceptionCodes.PROFILE_NOT_SET);
            } else if(incomplete.getProfile().getRole() == Role.BUSINESS) {
                throw new AuthException(
                        "Business profiles cannot be turned to user account",
                        ExceptionCodes.ACCESS_DENIED
                );
            } else {
                if(passwordEncoder.matches(login.getPassword(), profileInfo.getPassword())) {
                    RequestProfile profile = AuthMapper.INSTANCE.profile(profileInfo);
                    profile.setDevice(login.getDevice());
                    profile.setPlatform(login.getPlatform());
                    profile.setPassword(login.getPassword());
                    profile.setEmailAddress(login.getEmailAddress());
                    profile.setPhoneInformation(AuthMapper.INSTANCE.phoneInformation(phoneInfo));
                    return this.getSignupAuthResponse(profile, incomplete);
                } else {
                    throw new AuthException("Incorrect user details", ExceptionCodes.INCORRECT_LOGIN);
                }
            }
        }
    }

    private static RequestProfile getRequestProfile(RequestLogin login, IncompleteProfile profileInfo) {
        RequestProfile profile = new RequestProfile();
        profile.setGender(profileInfo.getGender());
        profile.setLastName(profileInfo.getLastName());
        profile.setDevice(login.getDevice());
        profile.setPassword(profileInfo.getPassword());
        profile.setFcmToken(profileInfo.getFcmToken());
        profile.setPlatform(login.getPlatform());
        profile.setReferral(profileInfo.getReferralCode());
        profile.setFirstName(profileInfo.getFirstName());
        return profile;
    }
}
