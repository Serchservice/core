package com.serch.server.domains.bookmark;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.UUID;

@Data
public class BookmarkResponse {
    private String id;
    private String name;
    private String avatar;
    private UUID user;
    private String category;
    private Double rating;

    @JsonProperty("last_signed_in")
    private String lastSignedIn;
}
