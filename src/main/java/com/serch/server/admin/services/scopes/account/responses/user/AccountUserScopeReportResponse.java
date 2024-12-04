package com.serch.server.admin.services.scopes.account.responses.user;

import com.serch.server.admin.services.responses.CommonProfileResponse;
import com.serch.server.enums.company.IssueStatus;
import lombok.Data;

@Data
public class AccountUserScopeReportResponse {
    private String ticket;
    private String comment;
    private IssueStatus status;
    private CommonProfileResponse reporter;
}