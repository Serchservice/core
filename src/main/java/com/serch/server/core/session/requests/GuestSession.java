package com.serch.server.core.session.requests;

import com.serch.server.enums.shared.UseStatus;
import lombok.Data;

@Data
public class GuestSession {
    private Long id;
    private String key;
    private UseStatus status;

    public GuestSession(Long id, UseStatus status, String key) {
        this.id = id;
        this.key = key;
        this.status = status;
    }
}