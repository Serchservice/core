package com.serch.server.domains.company.requests;

import lombok.Data;

@Data
public class ServiceSuggestRequest {
    private String service;
    private String platform;
}
