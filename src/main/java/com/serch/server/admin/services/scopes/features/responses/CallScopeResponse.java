package com.serch.server.admin.services.scopes.features.responses;

import com.serch.server.admin.services.responses.CommonProfileResponse;
import com.serch.server.domains.conversation.responses.CallInformation;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CallScopeResponse extends CallInformation {
    private CommonProfileResponse caller;
    private CommonProfileResponse called;
}