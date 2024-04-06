package com.serch.server.services.account.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.models.auth.User;
import com.serch.server.models.auth.incomplete.Incomplete;

public interface BusinessProfileService {
    ApiResponse<String> createProfile(Incomplete incomplete, User user);
}
