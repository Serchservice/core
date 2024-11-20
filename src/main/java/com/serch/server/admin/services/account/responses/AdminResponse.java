package com.serch.server.admin.services.account.responses;

import com.serch.server.admin.services.team.responses.AdminActivityResponse;
import com.serch.server.admin.services.team.responses.AdminTeamResponse;
import lombok.Data;

import java.util.List;

@Data
public class AdminResponse {
    private AdminProfileResponse profile;
    private AdminTeamResponse team;
    private List<AdminActivityResponse> activities;
}