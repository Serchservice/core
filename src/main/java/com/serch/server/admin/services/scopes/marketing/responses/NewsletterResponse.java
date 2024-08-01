package com.serch.server.admin.services.scopes.marketing.responses;

import com.serch.server.enums.company.NewsletterStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NewsletterResponse {
    private Long id;
    private String emailAddress;
    private NewsletterStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
