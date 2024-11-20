package com.serch.server.admin.services.team.responses;

import com.serch.server.admin.services.account.responses.AdminProfileResponse;
import lombok.Data;

import java.util.List;

@Data
public class AdminGroupResponse {
    private String role;
    private List<AdminProfileResponse> admins;
}