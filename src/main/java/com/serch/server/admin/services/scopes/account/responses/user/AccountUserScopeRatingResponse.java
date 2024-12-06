package com.serch.server.admin.services.scopes.account.responses.user;

import com.serch.server.admin.enums.EventType;
import com.serch.server.admin.services.responses.CommonProfileResponse;
import lombok.Data;

@Data
public class AccountUserScopeRatingResponse {
    private Long id;
    private Double rating;
    private String comment;
    private String event;
    private EventType type;
    private CommonProfileResponse profile;
}