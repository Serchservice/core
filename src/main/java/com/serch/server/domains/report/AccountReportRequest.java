package com.serch.server.domains.report;

import lombok.Data;

import java.util.UUID;

@Data
public class AccountReportRequest {
    private String content;
    private UUID id;
    private String shop;
}
