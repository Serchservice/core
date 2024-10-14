package com.serch.server.admin.services.account.responses;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class AdminActivityResponse {
    private Long id;
    private String activity;
    private String associated;
    private String tag;
    private String label;
    private String header;
    private ZonedDateTime createdAt = ZonedDateTime.now();
    private ZonedDateTime updatedAt = ZonedDateTime.now();
}