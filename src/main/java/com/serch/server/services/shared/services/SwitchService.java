package com.serch.server.services.shared.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.auth.responses.AuthResponse;
import com.serch.server.services.shared.requests.SwitchRequest;
import com.serch.server.services.shared.responses.GuestResponse;

public interface SwitchService {
    ApiResponse<GuestResponse> switchToGuest(SwitchRequest request);
    ApiResponse<AuthResponse> switchToUser(SwitchRequest request);
}
