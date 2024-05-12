package com.serch.server.services.shared.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SharedLinkData {
    @JsonProperty("link_id")
    private String linkId;

    private String link;
    private String label;
    private String image;
    private String amount;
    private String status;
    private String category;
    private SharedProfileData provider;
    private SharedProfileData user;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}