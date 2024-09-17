package com.serch.server.services.auth.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.core.email.EmailService;
import com.serch.server.enums.auth.AuthMethod;
import com.serch.server.enums.auth.Role;
import com.serch.server.enums.email.EmailType;
import com.serch.server.exceptions.ExceptionCodes;
import com.serch.server.exceptions.auth.AuthException;
import com.serch.server.models.auth.User;
import com.serch.server.models.auth.incomplete.Incomplete;
import com.serch.server.models.email.SendEmail;
import com.serch.server.repositories.account.AccountDeleteRepository;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.repositories.auth.incomplete.IncompleteRepository;
import com.serch.server.services.account.services.AccountSettingService;
import com.serch.server.services.auth.requests.RequestEmailToken;
import com.serch.server.services.auth.requests.RequestLogin;
import com.serch.server.services.auth.requests.RequestSession;
import com.serch.server.services.auth.responses.AuthResponse;
import com.serch.server.services.auth.services.AccountStatusTrackerService;
import com.serch.server.services.auth.services.AuthService;
import com.serch.server.services.auth.services.SessionService;
import com.serch.server.services.auth.services.TokenService;
import com.serch.server.services.referral.services.ReferralProgramService;
import com.serch.server.utils.TimeUtil;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
@Transactional(propagation = Propagation.NESTED)
public class AuthImplementation implements AuthService {
    private final IncompleteRepository incompleteRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final SessionService sessionService;
    private final AccountStatusTrackerService trackerService;
    private final AccountSettingService accountSettingService;
    private final ReferralProgramService referralProgramService;
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final AccountDeleteRepository accountDeleteRepository;

    @Value("${application.security.otp-expiration-time}")
    protected Integer OTP_EXPIRATION_TIME;

    @Value("${application.security.otp-trials}")
    protected Integer MAXIMUM_OTP_TRIALS;

    @Override
    public void sendOtp(String emailAddress) {
        Optional<Incomplete> user = incompleteRepository.findByEmailAddress(emailAddress);
        String otp = tokenService.generateOtp();
        if (user.isPresent()) {
            if(user.get().getTrials() < MAXIMUM_OTP_TRIALS) {
                user.get().setToken(passwordEncoder.encode(otp));
                user.get().setUpdatedAt(TimeUtil.now());
                user.get().setTrials(user.get().getTrials() + 1);
                user.get().setTokenExpiresAt(TimeUtil.now().plusMinutes(OTP_EXPIRATION_TIME));
                incompleteRepository.save(user.get());
                sendEmail(emailAddress, otp);
            } else if(TimeUtil.isOtpExpired(user.get().getTokenExpiresAt(), "", OTP_EXPIRATION_TIME)) {
                user.get().setToken(passwordEncoder.encode(otp));
                user.get().setUpdatedAt(TimeUtil.now());
                user.get().setTrials(1);
                user.get().setTokenExpiresAt(TimeUtil.now().plusMinutes(OTP_EXPIRATION_TIME));
                incompleteRepository.save(user.get());
                sendEmail(emailAddress, otp);
            } else {
                throw new AuthException(
                        "You can request a new token in %s".formatted(TimeUtil.formatFutureTime(user.get().getTokenExpiresAt(), "")),
                        ExceptionCodes.EMAIL_NOT_VERIFIED
                );
            }
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
        System.out.println(incomplete);
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
            return new ApiResponse<>(
                    "Login with email and password",
                    user.get().getFirstName(),
                    HttpStatus.OK
            );
        } else {
            var incomplete = incompleteRepository.findByEmailAddress(email);
            if (incomplete.isPresent()) {
                if (!incomplete.get().isEmailConfirmed()) {
                    sendOtp(email);
                    throw new AuthException(
                            "An email was sent to your email. Use the OTP to continue with signup.",
                            ExceptionCodes.EMAIL_NOT_VERIFIED
                    );
                } else if (!incomplete.get().hasProfile()) {
                    throw new AuthException(
                            "Profile is not saved. Signup to create your profile",
                            ExceptionCodes.PROFILE_NOT_SET
                    );
                } else if (!incomplete.get().hasCategory()) {
                    throw new AuthException(
                            "Category is not selected. Signup to pick your skill",
                            ExceptionCodes.CATEGORY_NOT_SET
                    );
                } else {
                    throw new AuthException(
                            "You have not finished creating your account. Finish up now",
                            ExceptionCodes.ACCOUNT_NOT_CREATED
                    );
                }
            } else {
                sendOtp(email);
                throw new AuthException(
                        "An email was sent to your email. Use the OTP to continue with signup.",
                        ExceptionCodes.USER_NOT_FOUND
                );
            }
        }
    }

    @Override
    public ApiResponse<String> verifyEmailOtp(@NotNull RequestEmailToken request) {
        Incomplete incomplete = incompleteRepository.findByEmailAddress(request.getEmailAddress())
                .orElseThrow(() -> new AuthException("User not found", ExceptionCodes.USER_NOT_FOUND));
        if (TimeUtil.isOtpExpired(incomplete.getTokenExpiresAt(), "", OTP_EXPIRATION_TIME)) {
            throw new AuthException(
                    "OTP is expired. Request for another.",
                    ExceptionCodes.INCORRECT_TOKEN
            );
        } else {
            if (passwordEncoder.matches(request.getToken(), incomplete.getToken())) {
                incomplete.setTokenConfirmedAt(TimeUtil.now());
                incomplete.setUpdatedAt(TimeUtil.now());
                incompleteRepository.save(incomplete);
                return new ApiResponse<>("OTP confirmed", HttpStatus.OK);
            } else {
                throw new AuthException("Incorrect token", ExceptionCodes.INCORRECT_TOKEN);
            }
        }
    }

    @Override
    public ApiResponse<AuthResponse> authenticate(RequestLogin request, User user) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                request.getEmailAddress(),
                request.getPassword()
        );
        Authentication authentication = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return getAuthResponse(request, user);
    }

    @Override
    public ApiResponse<AuthResponse> getAuthResponse(RequestLogin request, User user) {
        user.setLastSignedIn(TimeUtil.now());
        user.setCountry(request.getCountry());
        user.setState(request.getState());
        user.setPasswordRecoveryToken(null);
        user.setPasswordRecoveryExpiresAt(null);
        user.setPasswordRecoveryConfirmedAt(null);
        userRepository.save(user);
        accountDeleteRepository.findByUser_EmailAddress(user.getEmailAddress()).ifPresent(accountDeleteRepository::delete);

        RequestSession requestSession = new RequestSession();
        requestSession.setMethod(AuthMethod.PASSWORD);
        requestSession.setUser(user);
        requestSession.setDevice(request.getDevice());
        return sessionService.generateSession(requestSession);
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
        User user = new User();
        user.setEmailAddress(incomplete.getEmailAddress());
        user.setPassword(incomplete.getProfile().getPassword());
        user.setEmailConfirmedAt(incomplete.getTokenConfirmedAt());
        user.setRole(role);
        user.setFirstName(incomplete.getProfile().getFirstName());
        user.setLastName(incomplete.getProfile().getLastName());
        return userRepository.save(user);
    }
}