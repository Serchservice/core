package com.serch.server.services.company.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.enums.company.IssueStatus;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class SpeakWithSerchResponse {
    private String ticket;
    private String label;
    private String time;
    private IssueStatus status;
    private Integer total;
    private List<IssueResponse> issues = new ArrayList<>();

    @JsonProperty("has_serch_message")
    private Boolean hasSerchMessage;

    @JsonProperty("updated_at")
    private ZonedDateTime updatedAt;

    @JsonProperty("created_at")
    private ZonedDateTime createdAt;
}