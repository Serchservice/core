package com.serch.server.domains.schedule.requests;

import com.serch.server.domains.trip.requests.OnlineRequest;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public class ScheduleRequest extends OnlineRequest {
    private UUID provider;
    private String time;
    private Integer amount;
}
