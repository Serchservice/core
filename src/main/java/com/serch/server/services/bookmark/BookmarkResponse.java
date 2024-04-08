package com.serch.server.services.bookmark;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class BookmarkResponse {
    private String id;
    private String name;
    private String avatar;
    private UUID user;
    private String category;
    private Double rating;

    @JsonProperty("last_seen")
    private String lastSeen;

    @JsonIgnore
    private LocalDateTime createdAt;
}
