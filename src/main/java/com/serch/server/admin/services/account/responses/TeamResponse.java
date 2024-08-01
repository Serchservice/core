package com.serch.server.admin.services.account.responses;

import lombok.Data;

import java.util.List;

@Data
public class TeamResponse {
    private AdminProfileResponse profile;
    private AdminTeamResponse team;
    private CompanyStructure structure;
    private List<AdminActivityGroupResponse> activities;
}
