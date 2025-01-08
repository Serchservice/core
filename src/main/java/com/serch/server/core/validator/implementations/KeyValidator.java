package com.serch.server.core.validator.implementations;

import com.serch.server.core.validator.KeyValidatorService;
import com.serch.server.exceptions.auth.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KeyValidator implements KeyValidatorService {
    @Value("${application.access.drive.apiKey}")
    private String DRIVE_API_KEY;

    @Value("${application.access.drive.secretKey}")
    private String DRIVE_SECRET_KEY;

    @Value("${application.access.guest.apiKey}")
    private String GUEST_API_KEY;

    @Value("${application.access.guest.secretKey}")
    private String GUEST_SECRET_KEY;

    @Value("${application.access.signature}")
    private String ACCESS_SIGNATURE;

    @Value("${application.access.identity}")
    private String ACCESS_IDENTITY;

    @Override
    public boolean isDrive(String apiKey, String secretKey) {
        return apiKey != null && !apiKey.isEmpty()
                && isValidDriveApiKey(apiKey)
                && secretKey != null && !secretKey.isEmpty()
                && isValidDriveSecretKey(secretKey);
    }

    private boolean isValidDriveApiKey(String apiKey) {
        return DRIVE_API_KEY.equals(apiKey);
    }

    private boolean isValidDriveSecretKey(String apiKey) {
        return DRIVE_SECRET_KEY.equals(apiKey);
    }

    @Override
    public boolean isGuest(String apiKey, String secretKey) {
        return apiKey != null && !apiKey.isEmpty()
                && isValidGuestApiKey(apiKey)
                && secretKey != null && !secretKey.isEmpty()
                && isValidGuestSecretKey(secretKey);
    }

    @Override
    public boolean isSigned(String key) {
        if(key == null) {
            throw new AuthException("Request is missing core authorization access.");
        } else {
            boolean isSigned = key.startsWith(ACCESS_IDENTITY) && ACCESS_SIGNATURE.equals(key);
            if(isSigned) {
                return true;
            } else {
                throw new AuthException("Unauthorized request, access denied.");
            }
        }
    }

    private boolean isValidGuestApiKey(String apiKey) {
        return GUEST_API_KEY.equals(apiKey);
    }

    private boolean isValidGuestSecretKey(String apiKey) {
        return GUEST_SECRET_KEY.equals(apiKey);
    }
}