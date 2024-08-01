package com.serch.server.admin.services.scopes.admin.requests;

import com.serch.server.enums.account.AccountStatus;
import lombok.Data;

import java.util.UUID;

@Data
public class ChangeStatusRequest {
    private UUID id;
    private AccountStatus status;
}
