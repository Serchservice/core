package com.serch.server.core.token.implementations;

import com.serch.server.core.token.TokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

/**
 * Service responsible for generating tokens such as OTPs and refresh tokens.
 * It implements its wrapper class {@link TokenService}
 */
@Service
public class TokenImplementation implements TokenService {
    @Value("${application.security.refresh-token-length}")
    private Integer REFRESH_TOKEN_LENGTH;
    @Value("${application.security.refresh-token-characters}")
    private String REFRESH_TOKEN_CHARACTERS;
    @Value("${application.security.otp-token-characters}")
    private String OTP_TOKEN_CHARACTERS;
    @Value("${application.security.otp-token-length}")
    private String OTP_TOKEN_LENGTH;

    private final SecureRandom random = new SecureRandom();

    @Override
    public String generateOtp() {
        return generateCode(Integer.parseInt(OTP_TOKEN_LENGTH));
    }

    @Override
    public String generateCode(int length) {
        StringBuilder otp = new StringBuilder(length);
        for(int i = 0; i < length; i++) {
            int index = random.nextInt(OTP_TOKEN_CHARACTERS.length());
            otp.append(OTP_TOKEN_CHARACTERS.charAt(index));
        }

        return otp.toString();
    }

    @Override
    public String generateRefreshToken() {
        return generate(REFRESH_TOKEN_LENGTH);
    }

    @Override
    public String generate(int length) {
        StringBuilder token = new StringBuilder(length);
        for(int i = 0; i < length; i++) {
            int index = random.nextInt(REFRESH_TOKEN_CHARACTERS.length());
            token.append(REFRESH_TOKEN_CHARACTERS.charAt(index));
        }

        return token.toString();
    }
}