package com.serch.server.services.auth.services.implementations;

import com.serch.server.bases.ApiResponse;
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
import com.serch.server.services.auth.requests.RequestEmailToken;
import com.serch.server.services.auth.requests.RequestLogin;
import com.serch.server.services.auth.requests.RequestSession;
import com.serch.server.services.auth.responses.AuthResponse;
import com.serch.server.services.auth.services.AuthService;
import com.serch.server.services.auth.services.SessionService;
import com.serch.server.services.auth.services.TokenService;
import com.serch.server.services.email.services.EmailAuthService;
import com.serch.server.services.referral.services.ReferralProgramService;
import com.serch.server.utils.TimeUtil;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
 * @see EmailAuthService
 * @see ReferralProgramService
 * @see AccountDeleteRepository
 */
@Service
@RequiredArgsConstructor
public class AuthImplementation implements AuthService {
    private final IncompleteRepository incompleteRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final SessionService sessionService;
    private final ReferralProgramService referralProgramService;
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;
    private final EmailAuthService emailService;
    private final AccountDeleteRepository accountDeleteRepository;

    @Value("${application.security.otp-expiration-time}")
    protected Integer OTP_EXPIRATION_TIME;

    @Override
    public Incomplete sendOtp(String emailAddress) {
        var user = incompleteRepository.findByEmailAddress(emailAddress);
        String otp = tokenService.generateOtp();
        if(user.isPresent()) {
            if(TimeUtil.isOtpExpired(user.get().getTokenExpiresAt(), OTP_EXPIRATION_TIME)) {
                user.get().setToken(passwordEncoder.encode(otp));
                user.get().setUpdatedAt(LocalDateTime.now());
                user.get().setTokenExpiresAt(LocalDateTime.now().plusMinutes(OTP_EXPIRATION_TIME));
                incompleteRepository.save(user.get());
                sendEmail(emailAddress, otp);
                return user.get();
            } else {
                throw new AuthException(
                        "You can request a new token in %s".formatted(
                                TimeUtil.formatFutureTime(user.get().getTokenExpiresAt())
                        ),
                        ExceptionCodes.EMAIL_NOT_VERIFIED
                );
            }
        } else {
            var saved = getIncomplete(emailAddress, otp);
            sendEmail(emailAddress, otp);
            return saved;
        }
    }

    private void sendEmail(String emailAddress, String otp) {
        SendEmail email = new SendEmail();
        email.setContent(otp);
        email.setType(EmailType.SIGNUP);
        email.setTo(emailAddress);
        emailService.send(email);
    }

    private Incomplete getIncomplete(String emailAddress, String otp) {
        Incomplete incomplete = new Incomplete();
        incomplete.setEmailAddress(emailAddress);
        incomplete.setToken(passwordEncoder.encode(otp));
        incomplete.setTokenExpiresAt(LocalDateTime.now().plusMinutes(OTP_EXPIRATION_TIME));
        return incompleteRepository.save(incomplete);
    }

    @Override
    public ApiResponse<String> checkEmail(String email) {
        var user = userRepository.findByEmailAddressIgnoreCase(email);
        if(user.isPresent()) {
            return new ApiResponse<>("Login with email and password", HttpStatus.OK);
        } else {
            var incomplete = incompleteRepository.findByEmailAddress(email);
            if(incomplete.isPresent()) {
                if(!incomplete.get().isEmailConfirmed()) {
                    sendOtp(email);
                    throw new AuthException(
                            "Email not verified. Check your email",
                            ExceptionCodes.EMAIL_NOT_VERIFIED
                    );
                } else if(!incomplete.get().hasProfile()) {
                    throw new AuthException(
                            "Profile is not saved. Signup to create your profile",
                            ExceptionCodes.PROFILE_NOT_SET
                    );
                } else if(!incomplete.get().hasCategory()) {
                    throw new AuthException(
                            "Category is not selected. Signup to pick your skill",
                            ExceptionCodes.CATEGORY_NOT_SET
                    );
                } else if(!incomplete.get().hasAdditional()) {
                    throw new AuthException(
                            "Additional Information is not saved. Signup to get started",
                            ExceptionCodes.ADDITIONAL_NOT_SET
                    );
                } else {
                    throw new AuthException(
                            "You have not picked a Serch plan",
                            ExceptionCodes.PLAN_NOT_SET
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
        var incomplete = incompleteRepository.findByEmailAddress(request.getEmailAddress())
                .orElseThrow(() -> new AuthException("User not found", ExceptionCodes.USER_NOT_FOUND));
        if(TimeUtil.isOtpExpired(incomplete.getTokenExpiresAt(), OTP_EXPIRATION_TIME)) {
            throw new AuthException(
                    "OTP is expired. Request for another.",
                    ExceptionCodes.INCORRECT_TOKEN
            );
        } else {
            if(passwordEncoder.matches(request.getToken(), incomplete.getToken())) {
                incomplete.setTokenConfirmedAt(LocalDateTime.now());
                incomplete.setUpdatedAt(LocalDateTime.now());
                incompleteRepository.save(incomplete);

                if(incomplete.getRole() == Role.ASSOCIATE_PROVIDER) {
                    throw new AuthException(
                            "Finish signup for associate provider",
                            ExceptionCodes.ASSOCIATE_PROVIDER_EMAIL
                    );
                }
                return new ApiResponse<>("OTP confirmed", HttpStatus.OK);
            } else {
                throw new AuthException("Incorrect token", ExceptionCodes.INCORRECT_TOKEN);
            }
        }
    }

    @Override
    public ApiResponse<AuthResponse> authenticate(RequestLogin request, User user) {
        authenticate(request);

        RequestSession requestSession = new RequestSession();
        requestSession.setPlatform(request.getPlatform());
        requestSession.setMethod(AuthMethod.PASSWORD);
        requestSession.setUser(user);
        requestSession.setDevice(request.getDevice());
        var session = sessionService.generateSession(requestSession);

        user.setLastSignedIn(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setPasswordRecoveryToken(null);
        user.setPasswordRecoveryExpiresAt(null);
        user.setPasswordRecoveryConfirmedAt(null);
        userRepository.save(user);

        accountDeleteRepository.findByUser_EmailAddress(user.getEmailAddress())
                .ifPresent(accountDeleteRepository::delete);
        return new ApiResponse<>(AuthResponse.builder()
                .mfaEnabled(user.getMfaEnabled())
                .session(session.getData())
                .firstName(user.getFirstName())
                .role(user.getRole().getType())
                .recoveryCodesEnabled(user.getRecoveryCodeEnabled())
                .build()
        );
    }

    @Override
    public User getUserFromIncomplete(Incomplete incomplete, Role role) {
        User user = new User();
        user.setEmailAddress(incomplete.getEmailAddress());
        user.setPassword(incomplete.getProfile().getPassword());
        user.setEmailConfirmedAt(incomplete.getTokenConfirmedAt());
        user.setRole(role);
        user.setFirstName(incomplete.getProfile().getFirstName());
        user.setLastName(incomplete.getProfile().getLastName());
        User saved = userRepository.save(user);
        referralProgramService.create(saved);
        return saved;
    }

    private void authenticate(RequestLogin request) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                request.getEmailAddress(),
                request.getPassword()
        );
        Authentication authentication = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
