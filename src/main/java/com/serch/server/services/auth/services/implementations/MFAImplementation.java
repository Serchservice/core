package com.serch.server.services.auth.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.auth.AuthMethod;
import com.serch.server.enums.auth.Role;
import com.serch.server.exceptions.ExceptionCodes;
import com.serch.server.exceptions.auth.AuthException;
import com.serch.server.models.auth.User;
import com.serch.server.models.auth.mfa.MFAChallenge;
import com.serch.server.models.auth.mfa.MFAFactor;
import com.serch.server.models.auth.mfa.MFARecoveryCode;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.repositories.auth.mfa.MFAChallengeRepository;
import com.serch.server.repositories.auth.mfa.MFAFactorRepository;
import com.serch.server.repositories.auth.mfa.MFARecoveryCodeRepository;
import com.serch.server.services.auth.requests.RequestMFAChallenge;
import com.serch.server.services.auth.requests.RequestSession;
import com.serch.server.services.auth.responses.AuthResponse;
import com.serch.server.services.auth.responses.MFADataResponse;
import com.serch.server.services.auth.responses.MFAUsageResponse;
import com.serch.server.services.auth.services.MFAService;
import com.serch.server.services.auth.services.SessionService;
import com.serch.server.utils.UserUtil;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.recovery.RecoveryCodeGenerator;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MFAImplementation implements MFAService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final MFAFactorRepository mFAFactorRepository;
    private final MFAChallengeRepository mFAChallengeRepository;
    private final MFARecoveryCodeRepository mFARecoveryCodeRepository;
    private final SessionService sessionService;

    protected String generateSecret() {
        SecretGenerator secretGenerator = new DefaultSecretGenerator(64);
        return secretGenerator.generate();
    }

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

    protected boolean isCodeValid(String secret, String code) {
        return new DefaultCodeVerifier(
                new DefaultCodeGenerator(HashingAlgorithm.SHA512, 6),
                new SystemTimeProvider()
        ).isValidCode(secret, code);
    }

    @Override
    public ApiResponse<MFADataResponse> getMFAData() {
        var user = userRepository.findByEmailAddressIgnoreCase(UserUtil.getLoginUser())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if(user.getMfaEnabled()) {
            throw new AuthException("You have enabled MFA", ExceptionCodes.ACCESS_DENIED);
        } else {
            mFAFactorRepository.findByUser_Id(user.getId()).ifPresent(mFAFactorRepository::delete);
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
            saveMFAChallenge(request, factor);
            if(!isCodeValid(factor.getSecret(), request.getCode())) {
                if(!user.getMfaEnabled()) {
                    user.setMfaEnabled(true);
                    user.setUpdatedAt(LocalDateTime.now());
                    userRepository.save(user);
                }
                return sessionResponse(request, user);
            } else {
                throw new AuthException("Incorrect token", ExceptionCodes.INCORRECT_TOKEN);
            }
        }
    }

    private void saveMFAChallenge(RequestMFAChallenge request, MFAFactor factor) {
        MFAChallenge challenge = new MFAChallenge();
        challenge.setMfaFactor(factor);
        challenge.setIpAddress(request.getDevice().getIpAddress());
        challenge.setDeviceId(request.getDevice().getId());
        challenge.setDeviceName(request.getDevice().getName());
        challenge.setPlatform(request.getPlatform());
        mFAChallengeRepository.save(challenge);
    }

    private ApiResponse<AuthResponse> sessionResponse(RequestMFAChallenge request, User user) {
        RequestSession requestSession = new RequestSession();
        requestSession.setPlatform(request.getPlatform());
        requestSession.setMethod(AuthMethod.MFA);
        requestSession.setUser(user);
        requestSession.setDevice(request.getDevice());
        var session = sessionService.generateSession(requestSession);

        return new ApiResponse<>(AuthResponse.builder()
                .mfaEnabled(user.getMfaEnabled())
                .session(session.getData())
                .firstName(user.getFirstName())
                .recoveryCodesEnabled(user.getRecoveryCodeEnabled())
                .build()
        );
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
                        return new ApiResponse<>("Token is already used. Get another");
                    } else {
                        code.setIsUsed(true);
                        code.setUpdatedAt(LocalDateTime.now());
                        mFARecoveryCodeRepository.save(code);
                        saveMFAChallenge(request, factor);
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
    public ApiResponse<List<String>> getRecoveryCodes() {
        var user = userRepository.findByEmailAddressIgnoreCase(UserUtil.getLoginUser())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if(user.getMfaEnabled()) {
            if(user.getMfaFactor().getRecoveryCodes().isEmpty()) {
                return saveRecoveryCodes(user);
            } else if(user.getMfaFactor().getRecoveryCodes().stream().allMatch(MFARecoveryCode::getIsUsed)) {
                mFARecoveryCodeRepository.deleteAll(user.getMfaFactor().getRecoveryCodes());
                return saveRecoveryCodes(user);
            } else {
                throw new AuthException("MFA Recovery codes are not yet used");
            }
        } else {
            throw new AuthException("MFA is not enabled");
        }
    }

    @Override
    public ApiResponse<String> disable() {
        var user = userRepository.findByEmailAddressIgnoreCase(UserUtil.getLoginUser())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if(user.getMfaEnabled()) {
            user.setMfaEnabled(false);
            user.setRecoveryCodeEnabled(false);
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
            mFAFactorRepository.delete(user.getMfaFactor());
            return new ApiResponse<>("MFA disabled", HttpStatus.OK);
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

    protected String[] generateRecoveryCodes() {
        RecoveryCodeGenerator recoveryCodes = new RecoveryCodeGenerator();
        return recoveryCodes.generateCodes(8);
    }

    private ApiResponse<List<String>> saveRecoveryCodes(User user) {
        List<String> codes = new ArrayList<>();
        Arrays.stream(generateRecoveryCodes()).forEach(code -> {
            saveRecoveryCode(user, code, codes);
        });
        user.setRecoveryCodeEnabled(true);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        return new ApiResponse<>("Successful request", codes, HttpStatus.OK);
    }

    private void saveRecoveryCode(User user, String code, List<String> codes) {
        MFARecoveryCode recoveryCode = new MFARecoveryCode();
        recoveryCode.setCode(passwordEncoder.encode(code.toUpperCase()));
        recoveryCode.setFactor(user.getMfaFactor());
        mFARecoveryCodeRepository.save(recoveryCode);
        codes.add(code.toUpperCase());
    }
}
