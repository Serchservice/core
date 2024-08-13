package com.serch.server.admin.services.scopes.features.responses;

import com.serch.server.admin.services.responses.CommonProfileResponse;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RequestSharingScopeResponse {
    private String trip;
    private Long share;
    private CommonProfileResponse profile;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}