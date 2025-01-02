package com.serch.server.exceptions;

import com.serch.server.bases.ApiResponse;
import com.serch.server.exceptions.auth.AuthException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApiResponseExceptionHandler {
    private final ServerExceptionHandler handler;

    public ApiResponse<?> handle(Exception e) {
        // Handle various exceptions related to JWT processing and authentication
        return getApiResponse(e);
    }

    public ApiResponse<?> handle(Throwable e) {
        // Handle various exceptions related to JWT processing and authentication
        return getApiResponse(e);
    }

    private ApiResponse<?> getApiResponse(Throwable e) {
        ApiResponse<String> response = new ApiResponse<>(e.getMessage());

        switch (e) {
            case AuthException exception -> response = handler.handleAuthException(exception);
            case ExpiredJwtException exception -> response = handler.handleExpiredJwtException(exception);
            case UnsupportedJwtException exception -> response = handler.handleUnsupportedJwtException(exception);
            case SignatureException exception -> response = handler.handleSignatureException(exception);
            case StringIndexOutOfBoundsException exception -> response = handler.handleStringIndexOutOfBoundsException(exception);
            case MalformedJwtException exception -> response = handler.handleMalformedJwtException(exception);
            case MessageDeliveryException exception -> response = handler.handleMessageDeliveryException(exception);
            default -> {
            }
        }

        return response;
    }
}