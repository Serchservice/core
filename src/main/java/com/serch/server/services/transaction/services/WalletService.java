package com.serch.server.services.transaction.services;

import com.serch.server.models.auth.User;

public interface WalletService {
    void createWallet(User user);
}
