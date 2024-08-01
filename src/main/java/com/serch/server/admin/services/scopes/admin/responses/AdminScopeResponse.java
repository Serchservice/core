package com.serch.server.admin.services.scopes.admin.responses;

import com.serch.server.admin.services.account.responses.AdminResponse;
import com.serch.server.admin.services.account.responses.CompanyStructure;
import com.serch.server.admin.services.responses.AnalysisResponse;
import com.serch.server.admin.services.responses.auth.AccountAuthResponse;
import com.serch.server.admin.services.responses.auth.AccountMFAChallengeResponse;
import com.serch.server.admin.services.responses.auth.AccountMFAResponse;
import com.serch.server.admin.services.responses.auth.AccountSessionResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class AdminScopeResponse extends AdminResponse {
    private AccountAuthResponse auth;
    private CompanyStructure structure;
    private AccountMFAResponse mfa;
    private List<AccountMFAChallengeResponse> challenges;
    private List<AccountSessionResponse> sessions;
    private AnalysisResponse analysis;
}
