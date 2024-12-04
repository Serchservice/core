package com.serch.server.admin.services.scopes.account.responses.user;

import com.serch.server.admin.services.responses.auth.AccountAuthResponse;
import com.serch.server.admin.services.responses.auth.AccountMFAResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AccountUserScopeAuthResponse extends AccountAuthResponse {
    private AccountMFAResponse mfa;
}