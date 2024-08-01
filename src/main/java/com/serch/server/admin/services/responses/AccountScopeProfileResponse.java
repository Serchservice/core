package com.serch.server.admin.services.responses;

import com.serch.server.enums.account.AccountStatus;
import com.serch.server.enums.auth.Role;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AccountScopeProfileResponse {
    private UUID id;
    private String firstName;
    private String lastName;
    private String emailAddress;
    private AccountStatus status;
    private String avatar;
    private Role role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String accountCreatedAt = "N/A";
    private String accountUpdatedAt = "N/A";
    private String profileCreatedAt = "N/A";
    private String profileUpdatedAt = "N/A";
    private String emailConfirmedAt = "N/A";
    private String passwordUpdatedAt = "N/A";
    private String lastSignedIn = "N/A";
}
