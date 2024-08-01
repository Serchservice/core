package com.serch.server.admin.services.account.responses;

import com.serch.server.admin.services.responses.AccountScopeProfileResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AdminProfileResponse extends AccountScopeProfileResponse {
    private String empId;
    private Boolean shouldResendInvite;
}