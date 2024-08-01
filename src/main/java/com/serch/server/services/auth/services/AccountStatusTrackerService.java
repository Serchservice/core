package com.serch.server.services.auth.services;

import com.serch.server.models.auth.User;

public interface AccountStatusTrackerService {
    void create(User user);
}
