package com.serch.server.services.shared.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SharedStatusData {
    private String user;
    private String amount;
    private String status;
    private String label;
    private Double rating;
    private String trip;
    private String more;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}