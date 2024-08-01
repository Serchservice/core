package com.serch.server.admin.services.responses;

import lombok.Data;

@Data
public class Metric {
    private String header;
    private String count;
    private String feature;
}