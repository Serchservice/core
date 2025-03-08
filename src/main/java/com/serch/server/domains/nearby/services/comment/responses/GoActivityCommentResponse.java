package com.serch.server.domains.nearby.services.comment.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GoActivityCommentResponse {
    private Long id;
    private String comment;
    private String name;
    private String avatar;

    @JsonProperty("is_current_user")
    private Boolean isCurrentUser = false;
}