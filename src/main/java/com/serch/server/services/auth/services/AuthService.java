package com.serch.server.services.auth.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.auth.Role;
import com.serch.server.models.auth.User;
import com.serch.server.models.auth.incomplete.Incomplete;
import com.serch.server.services.auth.requests.RequestEmailToken;
import com.serch.server.services.auth.requests.RequestLogin;
import com.serch.server.services.auth.responses.AuthResponse;
import jakarta.validation.constraints.NotNull;

public interface AuthService {
    Incomplete sendOtp(String emailAddress);
    ApiResponse<String> checkEmail(String email);
    ApiResponse<String> verifyEmailOtp(@NotNull RequestEmailToken request);
    ApiResponse<AuthResponse> authenticate(RequestLogin request, User user);
    User getUserFromIncomplete(Incomplete incomplete, Role role);
}
