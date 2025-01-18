package com.serch.server.admin.services.scopes.support.responses;

import com.serch.server.admin.services.responses.CommonProfileResponse;
import com.serch.server.domains.company.responses.SpeakWithSerchResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SpeakWithSerchScopeResponse extends SpeakWithSerchResponse {
    private CommonProfileResponse assignedAdmin;
    private CommonProfileResponse resolvedBy;
    private CommonProfileResponse closedBy;
    private CommonProfileResponse user;
}
