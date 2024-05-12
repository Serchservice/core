package com.serch.server.services.auth.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.auth.AuthMethod;
import com.serch.server.enums.auth.Role;
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
import com.serch.server.utils.UserUtil;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.util.Utils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    /**
     * Generates a random secret for MFA.
     *
     * @return The generated secret.
     */
    protected String generateSecret() {
        SecretGenerator secretGenerator = new DefaultSecretGenerator(64);
        return secretGenerator.generate();
    }

    /**
     * Generates a QR code for the provided secret.
     *
     * @param secret The secret for generating the QR code.
     * @param role   The role of the user.
     * @return The data URI for the generated QR code.
     */
    @SneakyThrows
    protected String generateQrCode(String secret, Role role) {
        QrData data = new QrData.Builder()
                .label(UserUtil.getLoginUser())
                .secret(secret)
                .issuer("Serch " + role.getType())
                .algorithm(HashingAlgorithm.SHA512)
                .digits(6)
                .build();
        QrGenerator generator = new ZxingPngQrGenerator();
        byte[] imageData = generator.generate(data);
        return Utils.getDataUriForImage(imageData, generator.getImageMimeType());
    }

    /**
     * Checks if the provided MFA code is valid.
     *
     * @param secret The secret associated with the MFA code.
     * @param code   The MFA code to be validated.
     * @return {@code true} if the code is valid, otherwise {@code false}.
     */
    protected boolean isCodeValid(String secret, String code) {
        return new DefaultCodeVerifier(
                new DefaultCodeGenerator(),
                new SystemTimeProvider()
        ).isValidCode(secret, code);
    }

    @Override
    @Transactional
    public ApiResponse<MFADataResponse> getMFAData() {
        var user = userRepository.findByEmailAddressIgnoreCase(UserUtil.getLoginUser())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if(user.getMfaEnabled()) {
            throw new AuthException("You have enabled MFA", ExceptionCodes.ACCESS_DENIED);
        } else if(user.getMfaFactor() != null) {
            String qrCode = generateQrCode(user.getMfaFactor().getSecret(), user.getRole());
            return new ApiResponse<>(new MFADataResponse(user.getMfaFactor().getSecret(), qrCode));
        } else {
            String secret = generateSecret();
            String qrCode = generateQrCode(secret, user.getRole());

            MFAFactor factor = new MFAFactor();
            factor.setUser(user);
            factor.setSecret(secret);
            mFAFactorRepository.save(factor);
            return new ApiResponse<>(new MFADataResponse(secret, qrCode));
        }
    }

    @Override
    public ApiResponse<AuthResponse> validateCode(RequestMFAChallenge request) {
        var user = userRepository.findByEmailAddressIgnoreCase(UserUtil.getLoginUser())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        var factor = user.getMfaFactor();
        if(factor == null) {
            throw new AuthException("You have not enabled MFA", ExceptionCodes.ACCESS_DENIED);
        } else {
            MFAChallenge challenge = saveMFAChallenge(request, factor);
            if(isCodeValid(factor.getSecret(), request.getCode())) {
                if(!user.getMfaEnabled()) {
                    user.setMfaEnabled(true);
                    user.setUpdatedAt(LocalDateTime.now());
                    userRepository.save(user);

                    challenge.setVerifiedAt(LocalDateTime.now());
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
                        code.setUpdatedAt(LocalDateTime.now());
                        mFARecoveryCodeRepository.save(code);
                        MFAChallenge challenge = saveMFAChallenge(request, factor);
                        challenge.setVerifiedAt(LocalDateTime.now());
                        mFAChallengeRepository.save(challenge);
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
        user.setUpdatedAt(LocalDateTime.now());

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
            user.setMfaEnabled(false);
            user.setRecoveryCodeEnabled(false);
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
            mFAFactorRepository.delete(user.getMfaFactor());

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
    public ApiResponse<String> disableRecoveryCode() {
        var user = userRepository.findByEmailAddressIgnoreCase(UserUtil.getLoginUser())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if(user.getMfaEnabled() && user.getRecoveryCodeEnabled()) {
            user.setRecoveryCodeEnabled(false);
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
            mFAFactorRepository.delete(user.getMfaFactor());
            return new ApiResponse<>("MFA Recovery Codes disabled", HttpStatus.OK);
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
