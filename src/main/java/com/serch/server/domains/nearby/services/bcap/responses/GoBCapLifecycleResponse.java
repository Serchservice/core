package com.serch.server.domains.nearby.services.bcap.responses;

import lombok.Data;

@Data
public class GoBCapLifecycleResponse {
    private String id;
    private String activity;
    private Long interest;
    private String title;
    private String message;
}