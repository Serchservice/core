package com.serch.server.services.company.requests;

import lombok.Data;

@Data
public class ComplaintRequest {
    private String firstName;
    private String lastName;
    private String emailAddress;
    private String comment;
}