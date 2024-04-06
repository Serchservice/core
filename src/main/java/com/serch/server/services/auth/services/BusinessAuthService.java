package com.serch.server.services.auth.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.auth.requests.RequestAuth;
import com.serch.server.services.auth.requests.RequestLogin;
import com.serch.server.services.auth.responses.AuthResponse;

public interface BusinessAuthService {
    ApiResponse<AuthResponse> login(RequestLogin request);
    ApiResponse<AuthResponse> signup(RequestAuth auth);
    ApiResponse<AuthResponse> finishAssociateSignup(RequestAuth auth);
}
