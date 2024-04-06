package com.serch.server.services.transaction.services;

import com.serch.server.models.account.BusinessProfile;
import com.serch.server.models.account.Profile;

public interface WalletService {
    void createWallet(Profile profile);
    void createWallet(BusinessProfile profile);
}
