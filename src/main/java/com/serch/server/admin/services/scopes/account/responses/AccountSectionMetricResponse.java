package com.serch.server.admin.services.scopes.account.responses;

import com.serch.server.admin.services.responses.Metric;
import lombok.Data;

import java.util.List;

@Data
public class AccountSectionMetricResponse {
    private Metric metric;
    private List<String> alphabets;
}