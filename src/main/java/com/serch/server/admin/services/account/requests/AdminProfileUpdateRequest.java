package com.serch.server.admin.services.account.requests;

import lombok.Data;

@Data
public class AdminProfileUpdateRequest {
    private String firstName;
    private String lastName;
}