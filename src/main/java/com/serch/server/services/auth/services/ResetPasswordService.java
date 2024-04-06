package com.serch.server.services.auth.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.auth.requests.RequestPasswordChange;
import com.serch.server.services.auth.requests.RequestResetPassword;
import com.serch.server.services.auth.requests.RequestResetPasswordVerify;
import com.serch.server.services.auth.responses.AuthResponse;

public interface ResetPasswordService {
    ApiResponse<String> checkEmail(String emailAddress);
    ApiResponse<String> verifyToken(RequestResetPasswordVerify verify);
    ApiResponse<String> resetPassword(RequestResetPassword resetPassword);
    ApiResponse<AuthResponse> changePassword(RequestPasswordChange request);
}
