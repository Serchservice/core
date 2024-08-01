package com.serch.server.admin.services.account.responses;

import lombok.Data;

import java.util.List;

@Data
public class AdminResponse {
    private AdminProfileResponse profile;
    private AdminTeamResponse team;
    private List<AdminActivityResponse> activities;
}