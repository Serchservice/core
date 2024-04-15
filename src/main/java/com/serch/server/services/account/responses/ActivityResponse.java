package com.serch.server.services.account.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ActivityResponse {
    private String earning;
    private String trip;
    private String schedule;
    private String shared;
    private String rating;
}
