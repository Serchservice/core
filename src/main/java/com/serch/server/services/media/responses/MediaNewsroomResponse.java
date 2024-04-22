package com.serch.server.services.media.responses;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MediaNewsroomResponse {
    private String news;
    private String region;
    private String image;
    private String id;
    private String title;
    private String label;
    private LocalDateTime createdAt;
}
