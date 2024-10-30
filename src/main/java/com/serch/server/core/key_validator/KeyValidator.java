package com.serch.server.core.key_validator;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KeyValidator implements KeyValidatorService {
    @Value("${application.drive.key.api}")
    private String DRIVE_API_KEY;

    @Value("${application.drive.key.secret}")
    private String DRIVE_SECRET_KEY;

    @Value("${application.guest.key.api}")
    private String GUEST_API_KEY;

    @Value("${application.guest.key.secret}")
    private String GUEST_SECRET_KEY;

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

    private boolean isValidGuestApiKey(String apiKey) {
        return GUEST_API_KEY.equals(apiKey);
    }

    private boolean isValidGuestSecretKey(String apiKey) {
        return GUEST_SECRET_KEY.equals(apiKey);
    }
}