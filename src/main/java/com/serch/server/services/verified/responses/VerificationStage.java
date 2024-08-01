package com.serch.server.services.verified.responses;

import lombok.Data;

@Data
public class VerificationStage {
    private String name;
    private Boolean verified;
    private Boolean pending;
    private String type;
}