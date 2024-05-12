package com.serch.server.services.company.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.enums.company.IssueStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SpeakWithSerchResponse {
    private String ticket;
    private String label;
    private String time;
    private IssueStatus status;
    private List<IssueResponse> issues;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}