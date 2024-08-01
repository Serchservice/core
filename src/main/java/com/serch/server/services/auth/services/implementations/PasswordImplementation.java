package com.serch.server.services.auth.services.implementations;

import com.serch.server.admin.enums.ActivityMode;
import com.serch.server.admin.services.account.services.AdminActivityService;
import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.auth.AuthMethod;
import com.serch.server.enums.email.EmailType;
import com.serch.server.exceptions.ExceptionCodes;
import com.serch.server.exceptions.auth.AuthException;
import com.serch.server.models.email.SendEmail;
import com.serch.server.repositories.account.AccountDeleteRepository;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.services.auth.requests.RequestPasswordChange;
import com.serch.server.services.auth.requests.RequestResetPassword;
import com.serch.server.services.auth.requests.RequestResetPasswordVerify;
import com.serch.server.services.auth.requests.RequestSession;
import com.serch.server.services.auth.responses.AuthResponse;
import com.serch.server.services.auth.services.PasswordService;
import com.serch.server.services.auth.services.SessionService;
import com.serch.server.services.auth.services.TokenService;
import com.serch.server.core.email.services.EmailTemplateService;
import com.serch.server.utils.HelperUtil;
import com.serch.server.utils.TimeUtil;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Service responsible for implementing password reset functionality.
 * It implements its wrapper class {@link PasswordService}
 *
 * @see UserRepository
 * @see AccountDeleteRepository
 * @see TokenService
 * @see EmailTemplateService
 * @see PasswordEncoder
 * @see SessionService
 */
@Service
@RequiredArgsConstructor
public class PasswordImplementation implements PasswordService {
    private final UserRepository userRepository;
    private final AccountDeleteRepository accountDeleteRepository;
    private final TokenService tokenService;
    private final EmailTemplateService emailService;
    private final PasswordEncoder passwordEncoder;
    private final SessionService sessionService;
    private final AdminActivityService activityService;

    @Value("${application.security.otp-expiration-time}")
    protected Integer OTP_EXPIRATION_TIME;

    @Override
    public ApiResponse<String> checkEmail(String emailAddress) {
        var user = userRepository.findByEmailAddressIgnoreCase(emailAddress)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.check();
        if(TimeUtil.isOtpExpired(user.getPasswordRecoveryExpiresAt(), OTP_EXPIRATION_TIME)) {
            String otp = tokenService.generateOtp();
            user.setPasswordRecoveryToken(passwordEncoder.encode(otp));
            user.setPasswordRecoveryExpiresAt(LocalDateTime.now().plusMinutes(OTP_EXPIRATION_TIME));
            userRepository.save(user);

            SendEmail email = new SendEmail();
            email.setTo(user.getEmailAddress());
            email.setType(EmailType.RESET);
            email.setFirstName(user.getFirstName());
            email.setContent(otp);
            emailService.send(email);

            return new ApiResponse<>(
                    "Check your email for verification token",
                    user.getFirstName(),
                    HttpStatus.OK
            );
        } else {
            throw new AuthException(
                    "You can request a new token in %s".formatted(
                            TimeUtil.formatFutureTime(user.getPasswordRecoveryExpiresAt())
                    ),
                    ExceptionCodes.EMAIL_NOT_VERIFIED
            );
        }
    }

    @Override
    public ApiResponse<String> verifyToken(RequestResetPasswordVerify verify) {
        var user = userRepository.findByEmailAddressIgnoreCase(verify.getEmailAddress())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if(user.getPasswordRecoveryToken() == null) {
            throw new AuthException("Invalid access. Request for password reset OTP", ExceptionCodes.ACCESS_DENIED);
        } else {
            if(TimeUtil.isOtpExpired(user.getPasswordRecoveryExpiresAt(), OTP_EXPIRATION_TIME)) {
                throw new AuthException("OTP already expired");
            } else {
                if(passwordEncoder.matches(verify.getToken(), user.getPasswordRecoveryToken())) {
                    user.setPasswordRecoveryConfirmedAt(LocalDateTime.now());
                    user.setUpdatedAt(LocalDateTime.now());
                    userRepository.save(user);
                    return new ApiResponse<>(
                            "OTP successfully confirmed",
                            user.getFirstName(),
                            HttpStatus.OK
                    );
                } else {
                    throw new AuthException("Incorrect OTP");
                }
            }
        }
    }

    @Override
    public ApiResponse<String> resetPassword(RequestResetPassword resetPassword) {
        var user = userRepository.findByEmailAddressIgnoreCase(resetPassword.getEmailAddress())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if(user.getPasswordRecoveryToken() == null) {
            throw new AuthException("Invalid access. Request for password reset OTP", ExceptionCodes.ACCESS_DENIED);
        } else {
            if(user.getPasswordRecoveryConfirmedAt() == null) {
                throw new AuthException("Verify the OTP sent to your email", ExceptionCodes.ACCESS_DENIED);
            } else {
                if(passwordEncoder.matches(resetPassword.getPassword(), user.getPassword())) {
                    throw new AuthException(
                            "New password cannot be same as old password",
                            ExceptionCodes.INCORRECT_TOKEN
                    );
                } else if (!HelperUtil.validatePassword(resetPassword.getPassword())) {
                    throw new AuthException(
                            "Password must contain a lowercase, uppercase, special character and number",
                            ExceptionCodes.INCORRECT_TOKEN
                    );
                } else {
                    user.setPassword(passwordEncoder.encode(resetPassword.getPassword()));
                    user.setPasswordRecoveryExpiresAt(null);
                    user.setPasswordRecoveryToken(null);
                    user.setPasswordRecoveryConfirmedAt(null);
                    user.setLastUpdatedAt(LocalDateTime.now());
                    userRepository.save(user);
                    return new ApiResponse<>("Password successfully changed", HttpStatus.OK);
                }
            }
        }
    }

    @Override
    public ApiResponse<AuthResponse> changePassword(RequestPasswordChange request) {
        var user = userRepository.findByEmailAddressIgnoreCase(UserUtil.getLoginUser())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if(passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            if(passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
                throw new AuthException("New password cannot be same as old password");
            } else if(HelperUtil.validatePassword(request.getNewPassword())) {
                user.setPassword(passwordEncoder.encode(request.getNewPassword()));
                user.setLastUpdatedAt(LocalDateTime.now());

                accountDeleteRepository.findByUser_EmailAddress(UserUtil.getLoginUser())
                        .ifPresent(accountDeleteRepository::delete);

                if(user.isAdmin()) {
                    activityService.create(ActivityMode.PASSWORD_CHANGE, null, null, user);
                }

                RequestSession requestSession = new RequestSession();
                requestSession.setMethod(AuthMethod.PASSWORD_CHANGE);
                requestSession.setUser(user);
                requestSession.setDevice(request.getDevice());

                return sessionService.generateSession(requestSession);
            } else {
                throw new AuthException(
                        "New password must contain a lowercase, uppercase, special character and number",
                        ExceptionCodes.INCORRECT_TOKEN
                );
            }
        } else {
            throw new AuthException("Incorrect password", ExceptionCodes.INCORRECT_LOGIN);
        }
    }
}
