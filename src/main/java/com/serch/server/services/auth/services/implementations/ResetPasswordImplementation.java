package com.serch.server.services.auth.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.auth.AuthMethod;
import com.serch.server.enums.email.EmailType;
import com.serch.server.exceptions.ExceptionCodes;
import com.serch.server.exceptions.auth.AuthException;
import com.serch.server.models.email.SendEmail;
import com.serch.server.repositories.account.AccountDeleteRepository;
import com.serch.server.repositories.account.AccountRequestRepository;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.services.auth.requests.RequestPasswordChange;
import com.serch.server.services.auth.requests.RequestResetPassword;
import com.serch.server.services.auth.requests.RequestResetPasswordVerify;
import com.serch.server.services.auth.requests.RequestSession;
import com.serch.server.services.auth.responses.AuthResponse;
import com.serch.server.services.auth.services.ResetPasswordService;
import com.serch.server.services.auth.services.SessionService;
import com.serch.server.services.auth.services.TokenService;
import com.serch.server.services.email.services.EmailAuthService;
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

@Service
@RequiredArgsConstructor
public class ResetPasswordImplementation implements ResetPasswordService {
    private final UserRepository userRepository;
    private final AccountRequestRepository accountRequestRepository;
    private final AccountDeleteRepository accountDeleteRepository;
    private final TokenService tokenService;
    private final EmailAuthService emailService;
    private final PasswordEncoder passwordEncoder;
    private final SessionService sessionService;

    @Value("${application.security.otp-expiration-time}")
    protected Integer OTP_EXPIRATION_TIME;

    @Override
    public ApiResponse<String> checkEmail(String emailAddress) {
        var user = userRepository.findByEmailAddress(emailAddress)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if(TimeUtil.isOtpExpired(user.getPasswordRecoveryExpiresAt(), OTP_EXPIRATION_TIME)) {
            String otp = tokenService.generateOtp();
            user.setPasswordRecoveryToken(passwordEncoder.encode(otp));
            user.setUpdatedAt(LocalDateTime.now());
            user.setPasswordRecoveryExpiresAt(LocalDateTime.now().plusMinutes(OTP_EXPIRATION_TIME));
            userRepository.save(user);

            SendEmail email = new SendEmail();
            email.setTo(user.getEmailAddress());
            email.setType(EmailType.RESET);
            email.setFirstName(user.getFirstName());
            email.setContent(otp);
            emailService.send(email);

            return new ApiResponse<>("Check your email for OTP", HttpStatus.OK);
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
        var user = userRepository.findByEmailAddress(verify.getEmailAddress())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if(user.getPasswordRecoveryToken() == null) {
            throw new AuthException(
                    "Invalid access. Request for password reset OTP",
                    ExceptionCodes.ACCESS_DENIED
            );
        } else {
            if(TimeUtil.isOtpExpired(user.getPasswordRecoveryExpiresAt(), OTP_EXPIRATION_TIME)) {
                throw new AuthException("OTP already expired");
            } else {
                if(passwordEncoder.matches(verify.getToken(), user.getPasswordRecoveryToken())) {
                    user.setPasswordRecoveryConfirmedAt(LocalDateTime.now());
                    user.setUpdatedAt(LocalDateTime.now());
                    userRepository.save(user);
                    return new ApiResponse<>("OTP successfully confirmed",HttpStatus.OK);
                } else {
                    throw new AuthException("Incorrect OTP");
                }
            }
        }
    }

    @Override
    public ApiResponse<String> resetPassword(RequestResetPassword resetPassword) {
        var user = userRepository.findByEmailAddress(resetPassword.getEmailAddress())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if(user.getPasswordRecoveryToken() == null) {
            throw new AuthException(
                    "Invalid access. Request for password reset OTP",
                    ExceptionCodes.ACCESS_DENIED
            );
        } else {
            if(user.getPasswordRecoveryConfirmedAt() == null) {
                throw new AuthException(
                        "Verify the OTP sent to your email",
                        ExceptionCodes.ACCESS_DENIED
                );
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
                    user.setUpdatedAt(LocalDateTime.now());
                    userRepository.save(user);

                    accountRequestRepository.findByUser_EmailAddress(resetPassword.getEmailAddress())
                            .ifPresent(accountRequestRepository::delete);
                    return new ApiResponse<>("Password successfully changed", HttpStatus.OK);
                }
            }
        }
    }

    @Override
    public ApiResponse<AuthResponse> changePassword(RequestPasswordChange request) {
        var user = userRepository.findByEmailAddress(UserUtil.getLoginUser())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if(passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            if(passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
                throw new AuthException("New password cannot be same as old password");
            } else if(HelperUtil.validatePassword(request.getNewPassword())) {
                user.setPassword(passwordEncoder.encode(request.getNewPassword()));
                user.setUpdatedAt(LocalDateTime.now());

                accountRequestRepository.findByUser_EmailAddress(UserUtil.getLoginUser())
                        .ifPresent(accountRequestRepository::delete);
                accountDeleteRepository.findByUser_EmailAddress(UserUtil.getLoginUser())
                        .ifPresent(accountDeleteRepository::delete);

                RequestSession requestSession = new RequestSession();
                requestSession.setPlatform(request.getPlatform());
                requestSession.setMethod(AuthMethod.PASSWORD_CHANGE);
                requestSession.setUser(user);
                requestSession.setDevice(request.getDevice());
                var session = sessionService.generateSession(requestSession);

                return new ApiResponse<>(
                        "Password changed successfully",
                        AuthResponse.builder()
                                .mfaEnabled(user.getMfaEnabled())
                                .session(session.getData())
                                .firstName(user.getFirstName())
                                .recoveryCodesEnabled(user.getRecoveryCodeEnabled())
                                .build(),
                        HttpStatus.OK
                );
            } else {
                throw new AuthException(
                        "New password must contain a lowercase, uppercase, special character and number",
                        ExceptionCodes.INCORRECT_TOKEN
                );
            }
        } else {
            throw new AuthException(
                    "Incorrect password",
                    ExceptionCodes.INCORRECT_LOGIN
            );
        }
    }
}
