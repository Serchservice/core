package com.serch.server.core.session.requests;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class GoSession {
    private UUID id;
    private String key;
    private String emailAddress;
    private LocalDate expiry;

    public GoSession(UUID id, String emailAddress, String key, LocalDate expiry) {
        this.id = id;
        this.key = key;
        this.emailAddress = emailAddress;
        this.expiry = expiry;
    }
}