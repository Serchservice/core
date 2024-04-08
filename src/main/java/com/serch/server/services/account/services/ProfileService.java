package com.serch.server.services.account.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.models.account.Profile;
import com.serch.server.models.auth.User;
import com.serch.server.models.auth.incomplete.Incomplete;
import com.serch.server.services.account.requests.RequestCreateProfile;
import com.serch.server.services.auth.requests.RequestProfile;

public interface ProfileService {
    ApiResponse<Profile> createProfile(RequestCreateProfile request);
    ApiResponse<Profile> createProviderProfile(Incomplete incomplete, User user);
    ApiResponse<Profile> createUserProfile(RequestProfile request, User user, User referral);
}
