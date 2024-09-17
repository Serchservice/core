package com.serch.server.admin.services.scopes.features.responses;

import com.serch.server.admin.services.responses.CommonProfileResponse;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class RequestSharingScopeResponse {
    private String trip;
    private Long share;
    private CommonProfileResponse profile;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}