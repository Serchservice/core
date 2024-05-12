package com.serch.server.services.business.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.auth.responses.AuthResponse;
import com.serch.server.services.business.requests.ChangePasswordInviteRequest;
import com.serch.server.services.business.responses.VerifiedInviteResponse;

/**
 * This is the wrapper class for the implementation of associate provider authentication
 */
public interface BusinessAssociateAuthService {
    ApiResponse<VerifiedInviteResponse> verifyLink(String token);
    ApiResponse<AuthResponse> changePassword(ChangePasswordInviteRequest request);
}