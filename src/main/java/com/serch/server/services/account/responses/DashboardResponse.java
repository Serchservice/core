package com.serch.server.services.account.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardResponse {
    private String name;
    private String avatar;
    private String earning;
    private String trip;
    private String schedule;
    private String shared;
    private String rating;
}
