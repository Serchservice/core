package com.serch.server.domains.verified.responses;

import com.serch.server.enums.verified.VerificationStatus;
import lombok.Data;

@Data
public class VerificationResponse {
    private VerificationStatus status;
    private String message;
    private String link;
    private String remaining;
    private VerificationStage consent;
}