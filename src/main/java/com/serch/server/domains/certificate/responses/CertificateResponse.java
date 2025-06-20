package com.serch.server.domains.certificate.responses;

import lombok.Data;

import java.util.Map;

@Data
public class CertificateResponse {
    private Boolean isGenerated;
    private String reason;
    private CertificateData data;
    private Map<String, Boolean> instructions;
}