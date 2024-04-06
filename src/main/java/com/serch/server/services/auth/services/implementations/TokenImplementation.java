package com.serch.server.services.auth.services.implementations;

import com.serch.server.services.auth.services.TokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class TokenImplementation implements TokenService {
    @Value("${application.security.refresh-token-length}")
    protected Integer REFRESH_TOKEN_LENGTH;

    @Value("${application.security.refresh-token-characters}")
    protected String REFRESH_TOKEN_CHARACTERS;

    @Value("${application.security.otp-token-length}")
    protected Integer OTP_TOKEN_LENGTH;

    @Value("${application.security.otp-token-characters}")
    protected String OTP_TOKEN_CHARACTERS;

    protected SecureRandom random = new SecureRandom();

    @Override
    public String generateOtp() {
        StringBuilder otp = new StringBuilder(6);
        for(int i = 0; i < 6; i++) {
            int index = random.nextInt(OTP_TOKEN_CHARACTERS.length());
            otp.append(OTP_TOKEN_CHARACTERS.charAt(index));
        }
        return otp.toString();
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
        StringBuilder token = new StringBuilder(REFRESH_TOKEN_LENGTH);
        for(int i = 0; i < REFRESH_TOKEN_LENGTH; i++) {
            int index = random.nextInt(REFRESH_TOKEN_CHARACTERS.length());
            token.append(REFRESH_TOKEN_CHARACTERS.charAt(index));
        }
        return token.toString();
    }
}
