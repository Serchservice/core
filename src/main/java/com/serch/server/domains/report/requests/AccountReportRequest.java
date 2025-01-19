package com.serch.server.domains.report.requests;

import lombok.Data;

import java.util.UUID;

@Data
public class AccountReportRequest {
    private String content;
    private UUID id;
    private String shop;
}
