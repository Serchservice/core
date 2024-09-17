package com.serch.server.services.conversation.responses;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class CallPeriodResponse {
    private ZonedDateTime start;
    private ZonedDateTime end;
}
