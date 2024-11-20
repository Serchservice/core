package com.serch.server.admin.services.team.responses;

import com.serch.server.admin.services.account.responses.AdminProfileResponse;
import lombok.Data;

import java.util.List;

@Data
public class TeamResponse {
    private AdminProfileResponse profile;
    private AdminTeamResponse team;
    private CompanyStructure structure;
    private List<AdminActivityGroupResponse> activities;
}
