package com.serch.server.services.schedule.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ScheduleResponse {
    private String id;
    private String time;
    private String avatar;
    private String name;
    private String category;

    @JsonProperty("provider_name")
    private String providerName;

    @JsonProperty("provider_avatar")
    private String providerAvatar;
}
