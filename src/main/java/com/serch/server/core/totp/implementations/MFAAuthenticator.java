package com.serch.server.core.totp.implementations;

import com.serch.server.core.totp.MFAAuthenticatorService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class MFAAuthenticator implements MFAAuthenticatorService {
    @Override
    public String getRandomSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        String secretKey = new Base32().encodeToString(bytes);

        // make the secret key more human-readable by lower-casing and
        // inserting spaces between each group of 4 characters
        return secretKey.toLowerCase().replaceAll("(.{4})(?=.{4})", "$1 ");
    }

    @Override
    public String getTOTPCode(String secretKey) {
        String hexKey = Hex.encodeHexString(new Base32().decode(secretKey.replace(" ", "").toUpperCase()));
        String hexTime = Long.toHexString((System.currentTimeMillis() / 1000) / 30);

        return TOTP.generateTOTP(hexKey, hexTime, "6");
    }

    @Override
    public boolean isValid(String code, String secretKey) {
        return code.equals(getTOTPCode(secretKey));
    }
}
