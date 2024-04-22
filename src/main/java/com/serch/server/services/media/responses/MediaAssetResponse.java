package com.serch.server.services.media.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MediaAssetResponse {
    private String asset;
    private String title;

    @JsonProperty("is_black")
    private Boolean isBlack = false;
}