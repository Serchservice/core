package com.serch.server.admin.services.scopes.countries_in_serch.responses;

import com.serch.server.enums.account.AccountStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class CountryInSerchResponse {
    private String id;
    private String country;
    private String flag;
    private String emoji;
    private String code;
    private Integer dialCode;
    private List<StateInSerchResponse> states = new ArrayList<>();
    private AccountStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}