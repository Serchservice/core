package com.serch.server.admin.services.account.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.admin.services.responses.AccountScopeProfileResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AdminProfileResponse extends AccountScopeProfileResponse {
    @JsonProperty("emp_id")
    private String empId;

    @JsonProperty("should_resend_invite")
    private Boolean shouldResendInvite;
}