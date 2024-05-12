package com.serch.server.services.company.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class IssueResponse {
    private String message;
    private String label;

    @JsonProperty("is_serch")
    private Boolean isSerch;

    @JsonProperty("is_read")
    private Boolean isRead;

    @JsonProperty("sent_at")
    private LocalDateTime sentAt;
}