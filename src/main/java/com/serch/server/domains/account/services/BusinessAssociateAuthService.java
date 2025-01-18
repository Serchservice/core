package com.serch.server.domains.account.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.domains.auth.responses.AuthResponse;
import com.serch.server.domains.account.requests.AssociateInviteRequest;
import com.serch.server.domains.account.responses.VerifiedInviteResponse;

/**
 * Service interface for handling business associate authentication and invitation processes.
 */
public interface BusinessAssociateAuthService {
    /**
     * Verifies the validity of an invitation link using a token.
     *
     * @param token the unique token associated with the invitation link
     * @return an {@link ApiResponse} containing the {@link VerifiedInviteResponse} if the token is valid
     */
    ApiResponse<VerifiedInviteResponse> verifyLink(String token);

    /**
     * Accepts an invitation for a business associate based on the provided request.
     *
     * @param request the {@link AssociateInviteRequest} containing the details required to accept the invitation
     * @return an {@link ApiResponse} containing the {@link AuthResponse} after successfully accepting the invitation
     */
    ApiResponse<AuthResponse> acceptInvite(AssociateInviteRequest request);
}