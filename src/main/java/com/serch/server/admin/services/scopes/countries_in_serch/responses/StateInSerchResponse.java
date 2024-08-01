package com.serch.server.admin.services.scopes.countries_in_serch.responses;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StateInSerchResponse {
    private Long id;
    private String state;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}