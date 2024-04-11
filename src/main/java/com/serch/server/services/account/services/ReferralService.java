package com.serch.server.services.account.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.models.auth.User;
import com.serch.server.services.account.responses.ReferralResponse;

import java.util.List;

public interface ReferralService {
    void create(User referral, User referredBy);
    User verifyCode(String code);
    ApiResponse<String> verifyLink(String link);
    ApiResponse<List<ReferralResponse>> viewReferrals();
}
