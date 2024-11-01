package com.serch.server.core.validator.key;

/**
 * The KeyValidator interface defines methods to handle and validate API keys and request signatures
 * for secure communication between the client app and server. This includes verifying the authenticity of incoming
 * requests by validating API keys, signatures, and optionally the request path.
 */
public interface KeyValidatorService {
    /**
     * Determines if the request is intended for a drive-related operation, such as retrieving or modifying
     * information related to a drive service. This may involve verifying the API key, signature, and timestamp.
     *
     * @param apiKey    The API key sent with the request.
     * @param secretKey The Secret Key sent with the request.
     * @return true if the request is a valid drive operation request, false otherwise.
     */
    boolean isDrive(String apiKey, String secretKey);

    /**
     * Determines if the request is intended for a guest-related operation, such as retrieving or modifying
     * information related to a guest service. This may involve verifying the API key, signature, and timestamp.
     *
     * @param apiKey    The API key sent with the request.
     * @param secretKey The Secret Key sent with the request.
     * @return true if the request is a valid guest operation request, false otherwise.
     */
    boolean isGuest(String apiKey, String secretKey);
}