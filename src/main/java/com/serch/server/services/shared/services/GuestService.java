package com.serch.server.services.shared.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.auth.responses.AuthResponse;
import com.serch.server.services.shared.requests.GuestToUserRequest;
import com.serch.server.services.shared.responses.GuestActivityResponse;

public interface GuestService {
    ApiResponse<GuestActivityResponse> activity(String guestId, String linkId);
    ApiResponse<String> checkIfEmailIsConfirmed(String guestId);
    ApiResponse<AuthResponse> becomeAUser(GuestToUserRequest request);
    /// TODO:: Add connection method here with Websocket
}