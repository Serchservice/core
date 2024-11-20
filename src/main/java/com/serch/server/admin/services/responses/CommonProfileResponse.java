package com.serch.server.admin.services.responses;

import com.serch.server.enums.account.AccountStatus;
import com.serch.server.enums.auth.Role;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class CommonProfileResponse {
    private String id;
    private String firstName;
    private String lastName;
    private String name;
    private String emailAddress;
    private String avatar;
    private Role role;
    private String category;
    private Double rating;
    private String image;
    private AccountStatus status;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
