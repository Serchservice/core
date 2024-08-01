package com.serch.server.admin.services.scopes.support.responses;

import com.serch.server.admin.services.responses.CommonProfileResponse;
import com.serch.server.enums.company.IssueStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ComplaintResponse {
    private String id;
    private String comment;
    private IssueStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private CommonProfileResponse admin;
}