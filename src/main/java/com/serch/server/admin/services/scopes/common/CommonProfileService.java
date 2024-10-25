package com.serch.server.admin.services.scopes.common;

import com.serch.server.admin.services.responses.CommonProfileResponse;
import com.serch.server.models.auth.User;

public interface CommonProfileService {
    CommonProfileResponse fromTransaction(String id);
    CommonProfileResponse fromUser(User user);
}