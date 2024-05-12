package com.serch.server.services.shared.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SharedStatusData {
    private String user;
    private String provider;
    private String amount;
    private String more;
    private String status;
    private String label;
    private Double rating;
    private String trip;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}