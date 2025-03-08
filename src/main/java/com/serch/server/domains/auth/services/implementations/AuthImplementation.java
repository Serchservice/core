package com.serch.server.domains.auth.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.core.token.TokenService;
import com.serch.server.core.email.EmailService;
import com.serch.server.core.token.JwtService;
import com.serch.server.core.session.SessionService;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.enums.auth.AuthMethod;
import com.serch.server.enums.auth.Role;
import com.serch.server.enums.email.EmailType;
import com.serch.server.exceptions.ExceptionCodes;
import com.serch.server.exceptions.auth.AuthException;
import com.serch.server.mappers.AuthMapper;
import com.serch.server.models.auth.User;
import com.serch.server.models.auth.incomplete.*;
import com.serch.server.models.email.SendEmail;
import com.serch.server.repositories.account.AccountDeleteRepository;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.repositories.auth.incomplete.*;
import com.serch.server.domains.account.services.AccountSettingService;
import com.serch.server.domains.auth.requests.*;
import com.serch.server.domains.auth.responses.AuthResponse;
import com.serch.server.domains.auth.responses.PendingRegistrationResponse;
import com.serch.server.domains.auth.services.AccountStatusTrackerService;
import com.serch.server.domains.auth.services.AuthService;
import com.serch.server.domains.referral.services.ReferralProgramService;
import com.serch.server.utils.HelperUtil;
import com.serch.server.utils.TimeUtil;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Service implementation for managing authentication-related operations.
 * It implements its wrapper class {@link AuthService}
 *
 * @see IncompleteRepository
 * @see PasswordEncoder
 * @see UserRepository
 * @see SessionService
 * @see TokenService
 * @see AuthenticationManager
 * @see ReferralProgramService
 * @see AccountDeleteRepository
 * @see AccountSettingService
 */
@Service
@RequiredArgsConstructor
public class AuthImplementation implements AuthService {
    private final SessionService sessionService;
    private final AccountStatusTrackerService trackerService;
    private final AccountSettingService accountSettingService;
    private final ReferralProgramService referralProgramService;
    private final TokenService tokenService;
    private final EmailService emailService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final IncompleteRepository incompleteRepository;
    private final AccountDeleteRepository accountDeleteRepository;
    private final IncompleteProfileRepository incompleteProfileRepository;
    private final IncompletePhoneInformationRepository incompletePhoneInformationRepository;
    private final IncompleteReferralRepository incompleteReferralRepository;
    private final IncompleteCategoryRepository incompleteCategoryRepository;
    private final IncompleteSpecialtyRepository incompleteSpecialtyRepository;

    @Value("${application.security.otp-expiration-time}")
    protected Integer OTP_EXPIRATION_TIME;

    @Value("${application.security.otp-trials}")
    protected Integer MAXIMUM_OTP_TRIALS;

    @Value("${application.account.limit.specialty}")
    protected Integer SPECIALTY_LIMIT;

    @Override
    public void sendOtp(String emailAddress) {
        Optional<Incomplete> user = incompleteRepository.findByEmailAddress(emailAddress);
        String otp = tokenService.generateOtp();

        if (user.isPresent()) {
            int trials;
            if(user.get().getTrials() < MAXIMUM_OTP_TRIALS) {
                trials = user.get().getTrials() + 1;
            } else if(TimeUtil.isOtpExpired(user.get().getTokenExpiresAt(), "", OTP_EXPIRATION_TIME)) {
                trials = 1;
            } else {
                throw new AuthException(
                        "You can request a new token in %s".formatted(TimeUtil.formatFutureTime(user.get().getTokenExpiresAt(), "")),
                        ExceptionCodes.EMAIL_NOT_VERIFIED
                );
            }

            user.get().setToken(passwordEncoder.encode(otp));
            user.get().setUpdatedAt(TimeUtil.now());
            user.get().setTrials(trials);
            user.get().setTokenExpiresAt(TimeUtil.now().plusMinutes(OTP_EXPIRATION_TIME));
            incompleteRepository.save(user.get());

            sendEmail(emailAddress, otp);
        } else {
            createIncomplete(emailAddress, otp);
            sendEmail(emailAddress, otp);
        }
    }

    @Override
    public void sendEmail(String emailAddress, String otp) {
        SendEmail email = new SendEmail();
        email.setContent(otp);
        email.setType(EmailType.SIGNUP);
        email.setTo(emailAddress);

        emailService.send(email);
    }

    private void createIncomplete(String emailAddress, String otp) {
        Incomplete incomplete = new Incomplete();
        incomplete.setEmailAddress(emailAddress);
        incomplete.setToken(passwordEncoder.encode(otp));
        incomplete.setTrials(1);
        incomplete.setTokenExpiresAt(TimeUtil.now().plusMinutes(OTP_EXPIRATION_TIME));

        incompleteRepository.save(incomplete);
    }

    @Override
    public ApiResponse<String> checkEmail(String email) {
        var user = userRepository.findByEmailAddressIgnoreCase(email);

        if (user.isPresent()) {
            if(user.get().getRole() == Role.ASSOCIATE_PROVIDER && !user.get().getIsEmailConfirmed()) {
                throw new AuthException(
                        "Finish signup for associate provider. Check your email inbox or notify your admin",
                        ExceptionCodes.ASSOCIATE_PROVIDER_EMAIL
                );
            }

            return new ApiResponse<>("Login with email and password", user.get().getFirstName(), HttpStatus.OK);
        } else {
            var incomplete = incompleteRepository.findByEmailAddress(email);

            if(incomplete.isPresent()) {
                if (!incomplete.get().isEmailConfirmed()) {
                    sendOtp(email);

                    throw new AuthException("An email was sent to your email. Use the OTP to continue with signup.", ExceptionCodes.EMAIL_NOT_VERIFIED);
                } else if (!incomplete.get().hasProfile()) {
                    throw new AuthException("Profile is not saved. Signup to create your profile", ExceptionCodes.PROFILE_NOT_SET);
                } else {
                    throw new AuthException("You have not finished creating your account. Finish up now", ExceptionCodes.INCOMPLETE_REGISTRATION);
                }
            } else {
                sendOtp(email);

                throw new AuthException("An email was sent to your email. Use the OTP to continue with signup.", ExceptionCodes.USER_NOT_FOUND);
            }
        }
    }

    @Override
    public ApiResponse<String> verifyEmailOtp(@NotNull RequestEmailToken request) {
        Incomplete incomplete = incompleteRepository.findByEmailAddress(request.getEmailAddress())
                .orElseThrow(() -> new AuthException("User not found", ExceptionCodes.USER_NOT_FOUND));

        if(incomplete.isEmailConfirmed()) {
            throw new AuthException("User is already verified");
        } else if(TimeUtil.isOtpExpired(incomplete.getTokenExpiresAt(), "", OTP_EXPIRATION_TIME)) {
            throw new AuthException("OTP is expired. Request for another.", ExceptionCodes.INCORRECT_TOKEN);
        } else {
            if (passwordEncoder.matches(request.getToken(), incomplete.getToken())) {
                incomplete.setTokenConfirmedAt(TimeUtil.now());
                incomplete.setUpdatedAt(TimeUtil.now());
                incomplete.setTrials(0);
                incompleteRepository.save(incomplete);

                return new ApiResponse<>("OTP confirmed", HttpStatus.OK);
            } else {
                throw new AuthException("Incorrect token", ExceptionCodes.INCORRECT_TOKEN);
            }
        }
    }

    @Override
    @Transactional
    public ApiResponse<AuthResponse> authenticate(RequestLogin request, User user) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(request.getEmailAddress(), request.getPassword());
        Authentication authentication = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return getAuthResponse(request, user);
    }

    @Override
    @Transactional
    public ApiResponse<AuthResponse> getAuthResponse(RequestLogin request, User user) {
        updateUserInformation(request, user);

        RequestSession requestSession = new RequestSession();
        requestSession.setMethod(AuthMethod.PASSWORD);
        requestSession.setUser(user);
        requestSession.setDevice(request.getDevice());

        return sessionService.generateSession(requestSession);
    }

    @Transactional
    protected void updateUserInformation(RequestLogin request, User user) {
        user.setLastSignedIn(TimeUtil.now());
        user.setCountry(request.getCountry());
        user.setState(request.getState());
        user.setPasswordRecoveryToken(null);
        user.setPasswordRecoveryExpiresAt(null);
        user.setPasswordRecoveryConfirmedAt(null);
        userRepository.save(user);

        accountDeleteRepository.findByUser_EmailAddress(user.getEmailAddress()).ifPresent(accountDeleteRepository::delete);
    }

    @Override
    public User getUserFromIncomplete(Incomplete incomplete, Role role) {
        User saved = createNewUser(incomplete, role);
        trackerService.create(saved);
        referralProgramService.create(saved);
        accountSettingService.create(saved);

        return saved;
    }

    private User createNewUser(Incomplete incomplete, Role role) {
        User user = AuthMapper.INSTANCE.user(incomplete);
        user.setRole(role);

        return userRepository.save(user);
    }

    @Override
    public ApiResponse<PendingRegistrationResponse> verifyPendingRegistration(RequestLogin request) {
        var incomplete = incompleteRepository.findByEmailAddress(request.getEmailAddress())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if(incomplete.isEmailConfirmed()) {
            if(!incomplete.hasProfile()) {
                throw new AuthException("Profile is not saved. Signup to create your profile", ExceptionCodes.PROFILE_NOT_SET);
            }

            if(passwordEncoder.matches(request.getPassword(), incomplete.getProfile().getPassword())) {
                if (!incomplete.hasCategory()) {
                    return new ApiResponse<>(getRegistration(incomplete, false));
                } else {
                    return new ApiResponse<>(getRegistration(incomplete, true));
                }
            } else {
                throw new AuthException("Incorrect user response. Check your password credentials.");
            }
        } else {
            throw new AuthException("You have not confirmed your email", ExceptionCodes.EMAIL_NOT_VERIFIED);
        }
    }

    private PendingRegistrationResponse getRegistration(Incomplete incomplete, boolean isAccountNotCreated) {
        PendingRegistrationResponse response = new PendingRegistrationResponse();
        response.setToken(jwtService.generateToken(getRegistrationData(incomplete), incomplete.getEmailAddress()));
        response.setErrorCode(isAccountNotCreated ? ExceptionCodes.ACCOUNT_NOT_CREATED : ExceptionCodes.CATEGORY_NOT_SET);
        response.setMessage(isAccountNotCreated
                ? "You have not finished creating your account. Finish up now"
                : "Category is not selected. Signup to pick your skill"
        );

        return response;
    }

    private Map<String, Object> getRegistrationData(Incomplete incomplete) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", incomplete.getId());
        claims.put("role", incomplete.getRole().getType());

        return claims;
    }

    @Override
    public ApiResponse<PendingRegistrationResponse> saveProfile(RequestProviderProfile request) {
        var incomplete = incompleteRepository.findByEmailAddress(request.getEmailAddress())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if(incomplete.isEmailConfirmed()) {
            if(incomplete.hasProfile()) {
                throw new AuthException("You already have a profile. Confirm your email again", ExceptionCodes.ACCESS_DENIED);
            }

            if(HelperUtil.validatePassword(request.getPassword())) {
                saveProfile(request, incomplete);

                return new ApiResponse<>(
                        "Profile saved successfully",
                        getRegistration(incomplete, false),
                        HttpStatus.OK
                );
            } else {
                throw new AuthException(
                        "Password must contain a lowercase, uppercase, special character and number",
                        ExceptionCodes.INCORRECT_TOKEN
                );
            }
        } else {
            throw new AuthException("You have not confirmed your email", ExceptionCodes.EMAIL_NOT_VERIFIED);
        }
    }

    @Override
    public ApiResponse<PendingRegistrationResponse> saveCategory(RequestSerchCategory category) {
        var incomplete = incompleteRepository.findByEmailAddress(jwtService.getEmailFromToken(category.getToken()))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if(category.getCategory() == SerchCategory.USER) {
            throw new AuthException("You cannot be a Serch User in this platform");
        } else {
            if(incomplete.isEmailConfirmed()) {
                if(incomplete.hasProfile()) {
                    if(incomplete.hasCategory()) {
                        throw new AuthException("You already have a Serch category", ExceptionCodes.ACCESS_DENIED);
                    } else if(category.getSpecialties() != null && category.getSpecialties().size() > SPECIALTY_LIMIT) {
                        throw new AuthException("Specialties cannot be more than %s".formatted(SPECIALTY_LIMIT), ExceptionCodes.ACCESS_DENIED);
                    } else {
                        saveCategory(category, incomplete);

                        return new ApiResponse<>(
                                "Serch Category saved successfully",
                                getRegistration(incomplete, true),
                                HttpStatus.OK
                        );
                    }
                } else {
                    throw new AuthException("You don't have any profile", ExceptionCodes.PROFILE_NOT_SET);
                }
            } else {
                throw new AuthException("You have not confirmed your email", ExceptionCodes.EMAIL_NOT_VERIFIED);
            }
        }
    }

    private void saveProfile(RequestProviderProfile request, Incomplete incomplete) {
        if(request.getReferral() != null && !request.getReferral().isEmpty()) {
            saveReferral(request.getReferral(), incomplete);
        }

        insertProfile(request, incomplete);
        insertPhoneInformation(request, incomplete);
    }

    private void insertPhoneInformation(RequestProviderProfile request, Incomplete incomplete) {
        IncompletePhoneInformation phone = AuthMapper.INSTANCE.phoneInformation(request.getPhoneInformation());
        phone.setIncomplete(incomplete);
        incompletePhoneInformationRepository.save(phone);
    }

    private void insertProfile(RequestProviderProfile request, Incomplete incomplete) {
        IncompleteProfile profile = AuthMapper.INSTANCE.profile(request);
        profile.setIncomplete(incomplete);
        profile.setPassword(passwordEncoder.encode(request.getPassword()));

        incompleteProfileRepository.save(profile);
    }

    private void saveReferral(String code, Incomplete incomplete) {
        User referredBy = referralProgramService.verify(code);
        IncompleteReferral referral = new IncompleteReferral();
        referral.setIncomplete(incomplete);
        referral.setReferredBy(referredBy);

        incompleteReferralRepository.save(referral);
    }

    private void saveCategory(RequestSerchCategory category, Incomplete incomplete) {
        IncompleteCategory incompleteCategory = new IncompleteCategory();
        incompleteCategory.setCategory(category.getCategory());
        incompleteCategory.setIncomplete(incomplete);
        incompleteCategoryRepository.save(incompleteCategory);

        if(category.getSpecialties() != null) {
            category.getSpecialties().forEach(keyword -> {
                IncompleteSpecialty incompleteSpecialty = new IncompleteSpecialty();
                incompleteSpecialty.setSpecialty(keyword);
                incompleteSpecialty.setIncomplete(incomplete);
                incompleteSpecialtyRepository.save(incompleteSpecialty);
            });
        }
    }
}