package com.serch.server.domains.account.responses;

import lombok.Data;

@Data
public class BusinessInformationData {
    private String name;
    private String description;
    private String address;
    private String logo;
}