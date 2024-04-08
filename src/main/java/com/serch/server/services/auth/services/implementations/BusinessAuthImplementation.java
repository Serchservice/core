package com.serch.server.services.auth.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.auth.AuthMethod;
import com.serch.server.enums.auth.Role;
import com.serch.server.exceptions.ExceptionCodes;
import com.serch.server.exceptions.auth.AuthException;
import com.serch.server.models.account.Profile;
import com.serch.server.models.auth.User;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.repositories.auth.incomplete.IncompleteRepository;
import com.serch.server.services.account.services.BusinessProfileService;
import com.serch.server.services.account.services.ProfileService;
import com.serch.server.services.account.services.SpecialtyService;
import com.serch.server.services.auth.requests.RequestAuth;
import com.serch.server.services.auth.requests.RequestLogin;
import com.serch.server.services.auth.requests.RequestSession;
import com.serch.server.services.auth.responses.AuthResponse;
import com.serch.server.services.auth.services.AuthService;
import com.serch.server.services.auth.services.BusinessAuthService;
import com.serch.server.services.auth.services.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BusinessAuthImplementation implements BusinessAuthService {
    private final AuthService authService;
    private final SessionService sessionService;
    private final BusinessProfileService businessProfileService;
    private final ProfileService profileService;
    private final SpecialtyService specialtyService;
    private final UserRepository userRepository;
    private final IncompleteRepository incompleteRepository;

    @Override
    public ApiResponse<AuthResponse> login(RequestLogin request) {
        var user = userRepository.findByEmailAddress(request.getEmailAddress())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if(user.getRole() == Role.BUSINESS) {
            return authService.authenticate(request, user);
        } else {
            throw new AuthException("This email does not belong to a Serch Business", ExceptionCodes.ACCESS_DENIED);
        }
    }

    @Override
    public ApiResponse<AuthResponse> signup(RequestAuth auth) {
        var incomplete = incompleteRepository.findByEmailAddress(auth.getEmailAddress())
                .orElseThrow(() -> new AuthException("User not found"));

        if(incomplete.isEmailConfirmed()) {
            if(incomplete.hasProfile()) {
                if(incomplete.hasCategory()) {
                    User user = authService.getUserFromIncomplete(incomplete, Role.BUSINESS);
                    ApiResponse<String> response = businessProfileService.createProfile(incomplete, user);

                    if(response.getStatus().is2xxSuccessful()) {
                        RequestSession requestSession = new RequestSession();
                        requestSession.setPlatform(auth.getPlatform());
                        requestSession.setMethod(AuthMethod.PASSWORD);
                        requestSession.setUser(user);
                        requestSession.setDevice(auth.getDevice());
                        var session = sessionService.generateSession(requestSession);

                        incompleteRepository.delete(incomplete);
                        return new ApiResponse<>(AuthResponse.builder()
                                .mfaEnabled(user.getMfaEnabled())
                                .session(session.getData())
                                .role(user.getRole().getType())
                                .firstName(incomplete.getProfile().getFirstName())
                                .recoveryCodesEnabled(user.getRecoveryCodeEnabled())
                                .build()
                        );
                    } else {
                        throw new AuthException(response.getMessage());
                    }
                } else {
                    throw new AuthException(
                            "You don't have a Serch category", ExceptionCodes.CATEGORY_NOT_SET
                    );
                }
            } else {
                throw new AuthException("You don't have any profile", ExceptionCodes.PROFILE_NOT_SET);
            }
        } else {
            throw new AuthException("You have not confirmed your email", ExceptionCodes.EMAIL_NOT_VERIFIED);
        }
    }

    @Override
    public ApiResponse<AuthResponse> finishAssociateSignup(RequestAuth auth) {
        var incomplete = incompleteRepository.findByEmailAddress(auth.getEmailAddress())
                .orElseThrow(() -> new AuthException("User not found"));

        User user = authService.getUserFromIncomplete(incomplete, Role.ASSOCIATE_PROVIDER);
        ApiResponse<Profile> response = profileService.createProviderProfile(incomplete, user);
        if(response.getStatus().is2xxSuccessful()) {
            specialtyService.saveIncompleteSpecialties(incomplete, response);

            RequestSession requestSession = new RequestSession();
            requestSession.setPlatform(auth.getPlatform());
            requestSession.setMethod(AuthMethod.PASSWORD);
            requestSession.setUser(user);
            requestSession.setDevice(auth.getDevice());
            var session = sessionService.generateSession(requestSession);

            return new ApiResponse<>(AuthResponse.builder()
                    .mfaEnabled(user.getMfaEnabled())
                    .session(session.getData())
                    .role(user.getRole().getType())
                    .firstName(incomplete.getProfile().getFirstName())
                    .recoveryCodesEnabled(user.getRecoveryCodeEnabled())
                    .build()
            );
        } else {
            return new ApiResponse<>(response.getMessage());
        }
    }
}
