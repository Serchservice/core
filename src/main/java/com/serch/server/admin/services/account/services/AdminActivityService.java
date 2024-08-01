package com.serch.server.admin.services.account.services;

import com.serch.server.admin.enums.ActivityMode;
import com.serch.server.admin.models.Admin;
import com.serch.server.admin.services.account.responses.AdminActivityResponse;
import com.serch.server.models.auth.User;

import java.util.List;
import java.util.UUID;

public interface AdminActivityService {
    List<AdminActivityResponse> activities(UUID id);
    List<AdminActivityResponse> activities();
    void create(ActivityMode mode, String associated, String account, Admin admin);
    void create(ActivityMode mode, String associated, String account, User user);
}