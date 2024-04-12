package com.serch.server.services.auth.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.models.auth.User;
import com.serch.server.services.auth.requests.RequestLogin;
import com.serch.server.services.auth.requests.RequestProfile;
import com.serch.server.services.auth.responses.AuthResponse;

import java.time.LocalDateTime;

public interface UserAuthService {
    ApiResponse<AuthResponse> login(RequestLogin request);
    ApiResponse<AuthResponse> becomeAUser(RequestLogin request);
    ApiResponse<AuthResponse> signup(RequestProfile request);
    User getNewUser(RequestProfile profile, LocalDateTime confirmedAt);
    ApiResponse<AuthResponse> getAuthResponse(RequestProfile request, User newUser);
}
