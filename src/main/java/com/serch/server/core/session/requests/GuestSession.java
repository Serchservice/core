package com.serch.server.core.session.requests;

import com.serch.server.enums.shared.UseStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class GuestSession {
    private Long id;
    private String key;
    private UseStatus status;
    private LocalDate expiry;

    public GuestSession(Long id, UseStatus status, String key, LocalDate expiry) {
        this.id = id;
        this.key = key;
        this.status = status;
        this.expiry = expiry;
    }
}