package com.serch.server.services.account.services;

import com.serch.server.models.account.BusinessProfile;
import com.serch.server.models.account.Profile;

public interface ReferralService {
    void create(Profile profile, Profile referring);
    void create(Profile profile, BusinessProfile referring);
    void create(BusinessProfile profile, BusinessProfile referring);
    void create(BusinessProfile profile, Profile referring);
}
