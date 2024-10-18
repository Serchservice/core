package com.serch.server.services.auth.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.auth.AuthMethod;
import com.serch.server.enums.auth.Role;
import com.serch.server.exceptions.ExceptionCodes;
import com.serch.server.exceptions.auth.AuthException;
import com.serch.server.models.auth.User;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.repositories.auth.incomplete.IncompleteRepository;
import com.serch.server.services.account.services.AccountDeleteService;
import com.serch.server.services.account.services.BusinessService;
import com.serch.server.services.auth.requests.RequestBusinessProfile;
import com.serch.server.services.auth.requests.RequestLogin;
import com.serch.server.services.auth.requests.RequestSession;
import com.serch.server.services.auth.responses.AuthResponse;
import com.serch.server.services.auth.services.AuthService;
import com.serch.server.services.auth.services.BusinessAuthService;
import com.serch.server.core.session.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service implementation for managing authentication-related operations specific to businesses.
 * It implements its wrapper class {@link BusinessAuthService}
 *
 * @see AuthService
 * @see SessionService
 * @see BusinessService
 * @see UserRepository
 * @see IncompleteRepository
 */
@Service
@RequiredArgsConstructor
public class BusinessAuthImplementation implements BusinessAuthService {
    private final AuthService authService;
    private final AccountDeleteService deleteService;
    private final SessionService sessionService;
    private final BusinessService businessService;
    private final UserRepository userRepository;
    private final IncompleteRepository incompleteRepository;

    @Override
    public ApiResponse<AuthResponse> login(RequestLogin request) {
        var user = userRepository.findByEmailAddressIgnoreCase(request.getEmailAddress())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.check();
        if(user.getRole() == Role.BUSINESS) {
            return authService.authenticate(request, user);
        } else {
            throw new AuthException("This email does not belong to a Serch Business", ExceptionCodes.ACCESS_DENIED);
        }
    }

    @Override
    public ApiResponse<AuthResponse> signup(RequestBusinessProfile profile) {
        var incomplete = incompleteRepository.findByEmailAddress(profile.getEmailAddress())
                .orElseThrow(() -> new AuthException("User not found"));

        if(incomplete.isEmailConfirmed()) {
            if(incomplete.hasProfile()) {
                if(incomplete.hasCategory()) {
                   try {
                       User user = authService.getUserFromIncomplete(incomplete, Role.BUSINESS);
                       user.setCountry(profile.getCountry());
                       user.setState(profile.getState());
                       userRepository.save(user);
                       ApiResponse<String> response = businessService.createProfile(incomplete, user, profile);

                       if(response.getStatus().is2xxSuccessful()) {
                           RequestSession requestSession = new RequestSession();
                           requestSession.setMethod(AuthMethod.PASSWORD);
                           requestSession.setUser(user);
                           requestSession.setDevice(profile.getDevice());

                           incompleteRepository.delete(incomplete);
                           return sessionService.generateSession(requestSession);
                       } else {
                           deleteService.undo(incomplete.getEmailAddress());
                           throw new AuthException(response.getMessage());
                       }
                   } catch (DataIntegrityViolationException ignored) {
                       deleteService.undo(incomplete.getEmailAddress());
                       throw new AuthException("An error occurred while creating your business profile. Try again");
                   }
                } else {
                    throw new AuthException("You don't have a Serch category", ExceptionCodes.CATEGORY_NOT_SET);
                }
            } else {
                throw new AuthException("You don't have any profile", ExceptionCodes.PROFILE_NOT_SET);
            }
        } else {
            throw new AuthException("You have not confirmed your email", ExceptionCodes.EMAIL_NOT_VERIFIED);
        }
    }
}
