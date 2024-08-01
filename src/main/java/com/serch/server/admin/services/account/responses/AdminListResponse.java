package com.serch.server.admin.services.account.responses;

import lombok.Data;

import java.util.List;

@Data
public class AdminListResponse {
    private List<AdminGroupResponse> admins;
    private CompanyStructure structure;
}
