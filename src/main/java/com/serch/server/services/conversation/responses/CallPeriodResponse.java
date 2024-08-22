package com.serch.server.services.conversation.responses;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CallPeriodResponse {
    private LocalDateTime start;
    private LocalDateTime end;
}
