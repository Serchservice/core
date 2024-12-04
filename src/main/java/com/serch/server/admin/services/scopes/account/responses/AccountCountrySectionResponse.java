package com.serch.server.admin.services.scopes.account.responses;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AccountCountrySectionResponse {
    private String content;
    private List<String> colors = new ArrayList<>();
    private String label;
    private Double value;
}