package com.serch.server.domains.auth.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.core.jwt.JwtService;
import com.serch.server.enums.auth.AuthMethod;
import com.serch.server.enums.auth.Role;
import com.serch.server.exceptions.ExceptionCodes;
import com.serch.server.exceptions.auth.AuthException;
import com.serch.server.models.account.Profile;
import com.serch.server.models.auth.User;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.repositories.auth.incomplete.*;
import com.serch.server.domains.account.services.AccountDeleteService;
import com.serch.server.domains.account.services.AdditionalService;
import com.serch.server.domains.account.services.ProfileService;
import com.serch.server.domains.account.services.SpecialtyService;
import com.serch.server.domains.referral.services.ReferralProgramService;
import com.serch.server.domains.auth.requests.*;
import com.serch.server.domains.auth.responses.AuthResponse;
import com.serch.server.domains.auth.services.AuthService;
import com.serch.server.domains.auth.services.ProviderAuthService;
import com.serch.server.core.session.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service responsible for implementing provider authentication.
 * It implements its wrapper class {@link ProviderAuthService}
 * <p></p>
 * It interacts with {@link AuthService}, {@link ReferralProgramService}, {@link SessionService}, and others.
 *
 * @see AuthService
 * @see SessionService
 * @see ReferralProgramService
 * @see AdditionalService
 * @see SpecialtyService
 * @see ProfileService
 * @see PasswordEncoder
 * @see UserRepository
 * @see IncompleteRepository
 * @see IncompleteReferralRepository
 * @see IncompleteProfileRepository
 * @see IncompletePhoneInformationRepository
 * @see IncompleteCategoryRepository
 * @see IncompleteSpecialtyRepository
 */
@Service
@RequiredArgsConstructor
public class ProviderAuthImplementation implements ProviderAuthService {
    private final AuthService authService;
    private final SessionService sessionService;
    private final SpecialtyService specialtyService;
    private final ProfileService profileService;
    private final AccountDeleteService deleteService;
    private final AdditionalService additionalService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final IncompleteRepository incompleteRepository;

    @Override
    public ApiResponse<AuthResponse> login(RequestLogin request) {
        var user = userRepository.findByEmailAddressIgnoreCase(request.getEmailAddress())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.check();
        if(user.getRole() == Role.PROVIDER || user.getRole() == Role.ASSOCIATE_PROVIDER) {
            return authService.authenticate(request, user);
        } else {
            throw new AuthException("This email does not belong to a Serch Provider", ExceptionCodes.ACCESS_DENIED);
        }
    }

    @Override
    @Transactional
    public ApiResponse<AuthResponse> signup(RequestAdditionalInformation request) {
        var incomplete = incompleteRepository.findByEmailAddress(jwtService.getEmailFromToken(request.getToken()))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if(incomplete.isEmailConfirmed()) {
            if(incomplete.hasProfile()) {
                if(incomplete.hasCategory()) {
                    try {
                        User user = authService.getUserFromIncomplete(incomplete, Role.PROVIDER);
                        user.setCountry(request.getCountry());
                        user.setState(request.getState());
                        userRepository.save(user);
                        ApiResponse<Profile> response = profileService.createProviderProfile(incomplete, user);

                        if(response.getStatus().is2xxSuccessful()) {
                            additionalService.createAdditional(request, response.getData());
                            specialtyService.createSpecialties(incomplete, response.getData());

                            incompleteRepository.delete(incomplete);
                            incompleteRepository.flush();

                            RequestSession requestSession = new RequestSession();
                            requestSession.setMethod(AuthMethod.PASSWORD);
                            requestSession.setUser(user);
                            requestSession.setDevice(request.getDevice());

                            return sessionService.generateSession(requestSession);
                        } else {
                            deleteService.undo(incomplete.getEmailAddress());
                            return new ApiResponse<>(response.getMessage());
                        }
                    } catch (DataIntegrityViolationException ignored) {
                        deleteService.undo(incomplete.getEmailAddress());
                        throw new AuthException("An error occurred while creating your profile. Try again");
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