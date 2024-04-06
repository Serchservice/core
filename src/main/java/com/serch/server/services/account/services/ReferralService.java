package com.serch.server.services.account.services;

import com.serch.server.models.auth.User;

public interface ReferralService {
    void create(User referral, User referredBy);
    User verifyReferralCode(String code);
}
