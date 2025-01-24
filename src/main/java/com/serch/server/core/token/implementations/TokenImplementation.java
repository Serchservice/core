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
        return generate(length, OTP_TOKEN_CHARACTERS).toString();
    }

    @Override
    public String generateRefreshToken() {
        return generate(REFRESH_TOKEN_LENGTH);
    }

    @Override
    public String generate(int length) {
        return generate(length, REFRESH_TOKEN_CHARACTERS).toString();
    }

    private StringBuilder generate(int length, String characters) {
        StringBuilder token = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            token.append(characters.charAt(index));
        }

        return token;
    }

    @Override
    public String generate(String from, int length) {
        return generate(length, from.replaceAll(" ", "")).toString();
    }
}