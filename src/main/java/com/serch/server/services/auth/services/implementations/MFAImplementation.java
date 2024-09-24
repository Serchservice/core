package com.serch.server.services.auth.services.implementations;

import com.serch.server.admin.enums.ActivityMode;
import com.serch.server.admin.services.account.services.AdminActivityService;
import com.serch.server.bases.ApiResponse;
import com.serch.server.core.totp.MFAAuthenticatorService;
import com.serch.server.enums.auth.AuthMethod;
import com.serch.server.exceptions.ExceptionCodes;
import com.serch.server.exceptions.auth.AuthException;
import com.serch.server.mappers.AuthMapper;
import com.serch.server.models.auth.User;
import com.serch.server.models.auth.mfa.MFAChallenge;
import com.serch.server.models.auth.mfa.MFAFactor;
import com.serch.server.models.auth.mfa.MFARecoveryCode;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.repositories.auth.mfa.MFAChallengeRepository;
import com.serch.server.repositories.auth.mfa.MFAFactorRepository;
import com.serch.server.repositories.auth.mfa.MFARecoveryCodeRepository;
import com.serch.server.services.auth.requests.RequestDevice;
import com.serch.server.services.auth.requests.RequestMFAChallenge;
import com.serch.server.services.auth.requests.RequestSession;
import com.serch.server.services.auth.responses.AuthResponse;
import com.serch.server.services.auth.responses.MFADataResponse;
import com.serch.server.services.auth.responses.MFARecoveryCodeResponse;
import com.serch.server.services.auth.responses.MFAUsageResponse;
import com.serch.server.services.auth.services.MFAService;
import com.serch.server.services.auth.services.SessionService;
import com.serch.server.services.auth.services.TokenService;
import com.serch.server.utils.DatabaseUtil;
import com.serch.server.utils.TimeUtil;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service implementation for managing Multi-Factor Authentication (MFA) operations.
 * It implements its wrapper class {@link MFAService}
 *
 * @see PasswordEncoder
 * @see UserRepository
 * @see MFAFactorRepository
 * @see MFAChallengeRepository
 * @see MFARecoveryCodeRepository
 * @see SessionService
 * @see TokenService
 */
@Service
@RequiredArgsConstructor
public class MFAImplementation implements MFAService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final MFAFactorRepository mFAFactorRepository;
    private final MFAChallengeRepository mFAChallengeRepository;
    private final MFARecoveryCodeRepository mFARecoveryCodeRepository;
    private final SessionService sessionService;
    private final TokenService tokenService;
    private final AdminActivityService activityService;
    private final MFAAuthenticatorService authenticatorService;

    @Override
    public ApiResponse<MFADataResponse> getMFAData() {
        var user = userRepository.findByEmailAddressIgnoreCase(UserUtil.getLoginUser())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if(user.getMfaEnabled()) {
            throw new AuthException("You have enabled MFA", ExceptionCodes.ACCESS_DENIED);
        }
        return new ApiResponse<>(getMFAData(user));
    }

    @Override
    public MFADataResponse getMFAData(User user) {
        if(user.getMfaFactor() != null) {
            String secret = DatabaseUtil.decodeData(user.getMfaFactor().getSecret());
            String qrCode = authenticatorService.getQRCode(secret, user.getEmailAddress(), user.getRole());
            return new MFADataResponse(secret, String.format("data:image/png;base64,%s", qrCode));
        } else {
            String secret = authenticatorService.getRandomSecretKey();
            String qrCode = authenticatorService.getQRCode(secret, user.getEmailAddress(), user.getRole());

            MFAFactor factor = new MFAFactor();
            factor.setUser(user);
            factor.setSecret(DatabaseUtil.encodeData(secret));
            mFAFactorRepository.save(factor);
            return new MFADataResponse(secret, String.format("data:image/png;base64,%s", qrCode));
        }
    }

    @Override
    public ApiResponse<AuthResponse> validateCode(RequestMFAChallenge request) {
        var user = userRepository.findByEmailAddressIgnoreCase(UserUtil.getLoginUser())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if(user.getMfaFactor() == null) {
            throw new AuthException("You have not enabled MFA", ExceptionCodes.ACCESS_DENIED);
        } else {
            MFAChallenge challenge = saveMFAChallenge(request, user.getMfaFactor());
            if(authenticatorService.isValid(request.getCode(), DatabaseUtil.decodeData(user.getMfaFactor().getSecret()))) {
                if(!user.getMfaEnabled()) {
                    user.setMfaEnabled(true);
                    user.setUpdatedAt(TimeUtil.now());
                    userRepository.save(user);

                    if(user.isAdmin()) {
                        activityService.create(ActivityMode.MFA_LOGIN, null, null, user);
                    }

                    challenge.setVerifiedAt(TimeUtil.now());
                    mFAChallengeRepository.save(challenge);
                }
                return sessionResponse(request, user);
            } else {
                throw new AuthException("Incorrect token", ExceptionCodes.INCORRECT_TOKEN);
            }
        }
    }

    private MFAChallenge saveMFAChallenge(RequestMFAChallenge request, MFAFactor factor) {
        MFAChallenge challenge = AuthMapper.INSTANCE.challenge(request.getDevice());
        challenge.setMfaFactor(factor);
        return mFAChallengeRepository.save(challenge);
    }

    private ApiResponse<AuthResponse> sessionResponse(RequestMFAChallenge request, User user) {
        RequestSession requestSession = new RequestSession();
        requestSession.setMethod(AuthMethod.MFA);
        requestSession.setUser(user);
        requestSession.setDevice(request.getDevice());

        return sessionService.generateSession(requestSession);
    }

    @Override
    public ApiResponse<AuthResponse> validateRecoveryCode(RequestMFAChallenge request) {
        var user = userRepository.findByEmailAddressIgnoreCase(UserUtil.getLoginUser())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if(user.getMfaEnabled()) {
            var factor = user.getMfaFactor();
            for(var code : factor.getRecoveryCodes()) {
                if(passwordEncoder.matches(request.getCode(), code.getCode())) {
                    if(code.getIsUsed()) {
                        throw new AuthException("Token is already used. Get another");
                    } else {
                        code.setIsUsed(true);
                        code.setUpdatedAt(TimeUtil.now());
                        mFARecoveryCodeRepository.save(code);
                        MFAChallenge challenge = saveMFAChallenge(request, factor);
                        challenge.setVerifiedAt(TimeUtil.now());
                        mFAChallengeRepository.save(challenge);

                        if(user.isAdmin()) {
                            activityService.create(ActivityMode.MFA_LOGIN, null, null, user);
                        }
                        return sessionResponse(request, user);
                    }
                }
            }
        }
        throw new AuthException(
                "Multi-factor authentication is not enabled for user",
                ExceptionCodes.INCORRECT_TOKEN
        );
    }

    @Override
    public ApiResponse<List<MFARecoveryCodeResponse>> getRecoveryCodes() {
        var user = userRepository.findByEmailAddressIgnoreCase(UserUtil.getLoginUser())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if(user.getMfaEnabled()) {
            if(user.getMfaFactor().getRecoveryCodes() == null || user.getMfaFactor().getRecoveryCodes().isEmpty()) {
                return saveRecoveryCodes(user);
            } else if(user.getMfaFactor().getRecoveryCodes().stream().allMatch(MFARecoveryCode::getIsUsed)) {
                mFARecoveryCodeRepository.deleteAll(user.getMfaFactor().getRecoveryCodes());
                return saveRecoveryCodes(user);
            } else {
                return new ApiResponse<>(
                        user.getMfaFactor().getRecoveryCodes()
                                .stream()
                                .map(code -> {
                                    MFARecoveryCodeResponse response = AuthMapper.INSTANCE.response(code);
                                    response.setCode(DatabaseUtil.decodeData(code.getRecovery()));
                                    return response;
                                })
                                .toList()
                );
            }
        } else {
            throw new AuthException("MFA is not enabled");
        }
    }

    private List<String> generateRecoveryCodes() {
        List<String> referralCodes = new ArrayList<>();

        for (int i = 0; i < 16; i++) {
            String code;
            do {
                code = tokenService.generateCode(6).toUpperCase();
            } while (referralCodes.contains(code)); // Ensure uniqueness

            referralCodes.add(code);
        }
        return referralCodes;
    }

    /**
     * Saves MFA recovery codes for the user.
     *
     * @param user The user for whom the recovery codes are generated.
     * @return An API response containing the saved recovery codes.
     */
    private ApiResponse<List<MFARecoveryCodeResponse>> saveRecoveryCodes(User user) {
        List<String> codes = generateRecoveryCodes();
        codes.forEach(code -> saveRecoveryCode(user, code));
        user.setRecoveryCodeEnabled(true);
        user.setUpdatedAt(TimeUtil.now());

        if(!user.getRecoveryCodeEnabled()) {
            user.setRecoveryCodeEnabled(true);
        }
        userRepository.save(user);

        return new ApiResponse<>(codes.stream().map(code -> {
            MFARecoveryCodeResponse response = new MFARecoveryCodeResponse();
            response.setCode(code);
            response.setIsUsed(false);
            return response;
        }).toList());
    }

    /**
     * Saves a single MFA recovery code for the user.
     *
     * @param user  The user for whom the recovery code is saved.
     * @param code  The recovery code to be saved.
     */
    private void saveRecoveryCode(User user, String code) {
        MFARecoveryCode recoveryCode = new MFARecoveryCode();
        recoveryCode.setCode(passwordEncoder.encode(code));
        recoveryCode.setRecovery(DatabaseUtil.encodeData(code));
        recoveryCode.setFactor(user.getMfaFactor());
        mFARecoveryCodeRepository.save(recoveryCode);
    }

    @Override
    public ApiResponse<AuthResponse> disable(RequestDevice device) {
        var user = userRepository.findByEmailAddressIgnoreCase(UserUtil.getLoginUser())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if(user.getMfaEnabled()) {
            MFAFactor factor = user.getMfaFactor();
            mFAFactorRepository.delete(factor);
            mFAFactorRepository.flush();

            user.setMfaEnabled(false);
            user.setRecoveryCodeEnabled(false);
            user.setUpdatedAt(TimeUtil.now());
            userRepository.save(user);

            RequestSession requestSession = new RequestSession();
            requestSession.setMethod(AuthMethod.TOKEN);
            requestSession.setUser(user);
            requestSession.setDevice(device);

            return sessionService.generateSession(requestSession);
        } else {
            throw new AuthException("Error occurred while disabling MFA");
        }
    }

    @Override
    public ApiResponse<MFAUsageResponse> usage() {
        var user = userRepository.findByEmailAddressIgnoreCase(UserUtil.getLoginUser())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if(user.getMfaEnabled()) {
            return new ApiResponse<>(MFAUsageResponse.builder()
                    .used(
                            user.getMfaFactor()
                                .getRecoveryCodes()
                                .stream()
                                .filter(MFARecoveryCode::getIsUsed)
                                .toList().size()
                    )
                    .unused(
                            user.getMfaFactor().getRecoveryCodes().size() - user.getMfaFactor()
                                    .getRecoveryCodes()
                                    .stream()
                                    .filter(MFARecoveryCode::getIsUsed)
                                    .toList().size()
                    )
                    .total(user.getMfaFactor().getRecoveryCodes().size())
                    .build());
        } else {
            throw new AuthException("MFA is not enabled");
        }
    }
}
