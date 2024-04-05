package com.serch.server.services.media.responses;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MediaNewsroomResponse {
    private String news;
    private String region;
    private String image;
    private String key;
    private String title;
}
