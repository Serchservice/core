package com.serch.server.domains.nearby.services.auth.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.core.email.EmailService;
import com.serch.server.core.session.GoSessionService;
import com.serch.server.core.token.TokenService;
import com.serch.server.domains.nearby.services.auth.requests.GoAuthRequest;
import com.serch.server.domains.nearby.services.auth.requests.GoPasswordRequest;
import com.serch.server.domains.nearby.services.auth.responses.GoAuthResponse;
import com.serch.server.domains.nearby.services.auth.services.GoUserAuthService;
import com.serch.server.enums.email.EmailType;
import com.serch.server.exceptions.ExceptionCodes;
import com.serch.server.exceptions.auth.AuthException;
import com.serch.server.models.email.SendEmail;
import com.serch.server.domains.nearby.mappers.GoMapper;
import com.serch.server.domains.nearby.models.go.user.GoUser;
import com.serch.server.domains.nearby.repositories.go.GoUserRepository;
import com.serch.server.domains.nearby.services.account.services.GoLocationService;
import com.serch.server.domains.nearby.utils.GoUtils;
import com.serch.server.utils.AuthUtil;
import com.serch.server.utils.HelperUtil;
import com.serch.server.utils.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GoUserAuthImplementation implements GoUserAuthService {
    private final AuthUtil authUtil;
    private final GoUserRepository goUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final GoSessionService sessionService;
    private final GoLocationService locationService;
    private final TokenService tokenService;
    private final EmailService emailService;
    private final GoUtils goUtils;

    @Value("${application.security.otp-trials}")
    protected Integer MAXIMUM_OTP_TRIALS;

    @Value("${application.security.otp-expiration-time}")
    protected Integer OTP_EXPIRATION_TIME;

    @Override
    public ApiResponse<GoAuthResponse> login(GoAuthRequest request) {
        GoUser user = goUserRepository.findByEmailAddressIgnoreCase(request.getEmailAddress())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email address"));

        if(passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            if(user.isEmailAddressVerified()) {
                locationService.put(user, request.getAddress());

                return new ApiResponse<>(
                        goUtils.hasInterests(user) ? "Login successful" : ExceptionCodes.CATEGORY_NOT_SET,
                        sessionService.response(user),
                        HttpStatus.OK
                );
            } else {
                generateAndSendOtp(user, true);
                throw new AuthException("Email address is not verified. Verify your email address to continue", ExceptionCodes.EMAIL_NOT_VERIFIED);
            }
        } else {
            throw new AuthException("Invalid email or password");
        }
    }

    @Override
    public ApiResponse<String> signup(GoAuthRequest request) {
        Optional<GoUser> existing = goUserRepository.findByEmailAddressIgnoreCase(request.getEmailAddress());

        if(existing.isPresent()) {
            if(!existing.get().isEmailAddressVerified()) {
                generateAndSendOtp(existing.get(), true);
                throw new AuthException("Email address is not verified. Verify your email address to continue", ExceptionCodes.EMAIL_NOT_VERIFIED);
            }

            throw new AuthException("User already exists. Please login");
        } else if(request.getAddress() == null) {
            throw new AuthException("Your location information is needed to proceed");
        } else if(HelperUtil.validatePassword(request.getPassword())) {
            GoUser user = GoMapper.instance.user(request);
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user = goUserRepository.save(user);

            locationService.put(user, request.getAddress());

            generateAndSendOtp(user, true);
            return new ApiResponse<>("Verify your email address to complete signup", HttpStatus.OK);
        } else {
            throw new AuthException("Password must contain a lowercase, uppercase, special character and number", ExceptionCodes.INCORRECT_TOKEN);
        }
    }

    private void generateAndSendOtp(GoUser user, boolean isSignup) {
        String otp = tokenService.generateOtp();

        if(isSignup) {
            int trials;
            if(user.getTrials() < MAXIMUM_OTP_TRIALS) {
                trials = user.getTrials() + 1;
            } else if(TimeUtil.isOtpExpired(user.getEmailConfirmationTokenExpiresAt(), "", OTP_EXPIRATION_TIME)) {
                trials = 1;
            } else {
                throw new AuthException(
                        "You can request a new token in %s".formatted(TimeUtil.formatFutureTime(user.getEmailConfirmationTokenExpiresAt(), "")),
                        ExceptionCodes.EMAIL_NOT_VERIFIED
                );
            }

            user.setEmailConfirmationToken(passwordEncoder.encode(otp));
            user.setUpdatedAt(TimeUtil.now());
            user.setTrials(trials);
            user.setEmailConfirmationTokenExpiresAt(TimeUtil.now().plusMinutes(OTP_EXPIRATION_TIME));
            goUserRepository.save(user);
        } else {
            int trials;
            if(user.getTrials() < MAXIMUM_OTP_TRIALS) {
                trials = user.getTrials() + 1;
            } else if(TimeUtil.isOtpExpired(user.getPasswordRecoveryTokenExpiresAt(), "", OTP_EXPIRATION_TIME)) {
                trials = 1;
            } else {
                throw new AuthException(
                        "You can request a new token in %s".formatted(TimeUtil.formatFutureTime(user.getPasswordRecoveryTokenExpiresAt(), "")),
                        ExceptionCodes.EMAIL_NOT_VERIFIED
                );
            }

            user.setPasswordRecoveryToken(passwordEncoder.encode(otp));
            user.setUpdatedAt(TimeUtil.now());
            user.setTrials(trials);
            user.setPasswordRecoveryTokenExpiresAt(TimeUtil.now().plusMinutes(OTP_EXPIRATION_TIME));
            goUserRepository.save(user);
        }

        send(user.getEmailAddress(), otp, isSignup);
    }

    private void send(String emailAddress, String otp, boolean isSignup) {
        SendEmail email = new SendEmail();
        email.setContent(otp);
        email.setType(isSignup ? EmailType.SIGNUP : EmailType.RESET_PASSWORD);
        email.setTo(emailAddress);
        email.setIsNearby(true);

        emailService.send(email);
    }

    @Override
    public ApiResponse<GoAuthResponse> verifySignupToken(String emailAddress, String token) {
        GoUser user = goUserRepository.findByEmailAddressIgnoreCase(emailAddress)
                .orElseThrow(() -> new AuthException("User not found"));

        if(user.isEmailAddressVerified()) {
            throw new AuthException("User is already verified. Log in instead.");
        } else if(TimeUtil.isOtpExpired(user.getEmailConfirmationTokenExpiresAt(), "", OTP_EXPIRATION_TIME)) {
            throw new AuthException("OTP is expired. Request for another.", ExceptionCodes.INCORRECT_TOKEN);
        } else {
            if(passwordEncoder.matches(token, user.getEmailConfirmationToken())) {
                user.setEmailConfirmedAt(TimeUtil.now());
                user.setEmailConfirmationToken(null);
                user.setUpdatedAt(TimeUtil.now());
                user.setTrials(0);
                goUserRepository.save(user);

                return new ApiResponse<>("OTP confirmed", sessionService.response(user), HttpStatus.OK);
            } else {
                throw new AuthException("Incorrect token", ExceptionCodes.INCORRECT_TOKEN);
            }
        }
    }

    @Override
    public ApiResponse<GoAuthResponse> refreshToken(String token) {
        GoUser user = goUserRepository.findByEmailAddressIgnoreCase(sessionService.decode(token).getEmailAddress())
                .orElseThrow(() -> new AuthException("User not found", ExceptionCodes.USER_NOT_FOUND));

        return new ApiResponse<>("Token refreshed", sessionService.response(user), HttpStatus.OK);
    }

    @Override
    public ApiResponse<String> forgotPassword(String emailAddress) {
        GoUser user = goUserRepository.findByEmailAddressIgnoreCase(emailAddress)
                .orElseThrow(() -> new AuthException("User not found"));

        generateAndSendOtp(user, false);
        return new ApiResponse<>("OTP is being sent to your email address", HttpStatus.OK);
    }

    @Override
    public ApiResponse<String> verifyToken(String emailAddress, String token) {
        GoUser user = goUserRepository.findByEmailAddressIgnoreCase(emailAddress)
                .orElseThrow(() -> new AuthException("User not found"));

        if (TimeUtil.isOtpExpired(user.getPasswordRecoveryTokenExpiresAt(), "", OTP_EXPIRATION_TIME)) {
            throw new AuthException("OTP is expired. Request for another.", ExceptionCodes.INCORRECT_TOKEN);
        } else {
            if (passwordEncoder.matches(token, user.getPasswordRecoveryToken())) {
                user.setPasswordRecoveryTokenConfirmedAt(TimeUtil.now());
                user.setPasswordRecoveryToken(null);
                user.setUpdatedAt(TimeUtil.now());
                user.setTrials(0);
                goUserRepository.save(user);

                return new ApiResponse<>("OTP confirmed", HttpStatus.OK);
            } else {
                throw new AuthException("Incorrect token", ExceptionCodes.INCORRECT_TOKEN);
            }
        }
    }

    @Override
    public ApiResponse<String> resetPassword(GoAuthRequest request) {
        GoUser user = goUserRepository.findByEmailAddressIgnoreCase(request.getEmailAddress())
                .orElseThrow(() -> new AuthException("User not found"));

        if(user.getPasswordRecoveryTokenConfirmedAt() != null) {
            if(passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new AuthException("New password cannot be same as the old password");
            } else if(HelperUtil.validatePassword(request.getPassword())) {
                user.setPassword(passwordEncoder.encode(request.getPassword()));
                user.setPasswordRecoveryTokenConfirmedAt(null);
                user.setUpdatedAt(TimeUtil.now());

                locationService.put(user, request.getAddress());
                goUserRepository.save(user);

                return new ApiResponse<>("Password reset is successful. Continue to login", HttpStatus.OK);
            } else {
                throw new AuthException("Password must contain a lowercase, uppercase, special character and number", ExceptionCodes.INCORRECT_TOKEN);
            }
        } else {
            throw new AuthException("Password recovery has not been requested by this user");
        }
    }

    @Override
    public ApiResponse<String> resend(String emailAddress, Boolean isSignup) {
        GoUser user = goUserRepository.findByEmailAddressIgnoreCase(emailAddress)
                .orElseThrow(() -> new AuthException("User not found"));

        if(isSignup && user.isEmailAddressVerified()) {
            throw new AuthException("User is already verified, so token cannot be sent");
        } else if(!isSignup && user.getPasswordRecoveryToken() == null) {
            throw new AuthException("User has not requested a password recovery process");
        } else {
            generateAndSendOtp(user, isSignup);
            return new ApiResponse<>("OTP sent. Check your email inbox", HttpStatus.OK);
        }
    }

    @Override
    public ApiResponse<GoAuthResponse> changePassword(GoPasswordRequest request) {
        GoUser user = authUtil.getGoUser();

        if(!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new AuthException("Old password does not match");
        } else if(request.getNewPassword().equalsIgnoreCase(request.getOldPassword())) {
            throw new AuthException("New password is same as old password");
        } else if(HelperUtil.validatePassword(request.getNewPassword())) {
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            user.setUpdatedAt(TimeUtil.now());

            return new ApiResponse<>("Password changed successfully", sessionService.response(user), HttpStatus.OK);
        } else {
            throw new AuthException("Password must contain a lowercase, uppercase, special character and number", ExceptionCodes.INCORRECT_TOKEN);
        }
    }
}