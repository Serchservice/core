package com.serch.server.admin.services.responses;

import com.serch.server.enums.account.AccountStatus;
import com.serch.server.enums.auth.Role;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class AccountScopeProfileResponse extends AccountScopeDetailResponse {
    private UUID id;
    private AccountStatus status;
    private Role role;
}