package com.serch.server.admin.services.scopes.support.responses;

import lombok.Data;

import java.util.List;

@Data
public class ComplaintScopeResponse {
    private String emailAddress;
    private String firstName;
    private String lastName;
    private List<ComplaintResponse> complaints;
}