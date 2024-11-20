package com.serch.server.admin.services.team.responses;

import lombok.Data;

import java.util.List;

@Data
public class AdminListResponse {
    private List<AdminGroupResponse> admins;
    private CompanyStructure structure;
}
