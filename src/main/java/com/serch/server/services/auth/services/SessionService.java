package com.serch.server.services.auth.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.auth.requests.RequestSession;
import com.serch.server.services.auth.responses.SessionResponse;

import java.util.UUID;

public interface SessionService {
    void revokeAllRefreshTokens(UUID userId);
    void revokeAllSessions(UUID userId);
    ApiResponse<SessionResponse> generateSession(RequestSession request);
    ApiResponse<SessionResponse> refreshSession(String token);
    ApiResponse<String> validateSession(String token);
    void updateLastSeen();
    void signOut();
}
