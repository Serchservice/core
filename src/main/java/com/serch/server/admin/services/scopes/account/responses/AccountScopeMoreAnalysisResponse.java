package com.serch.server.admin.services.scopes.account.responses;

import com.serch.server.admin.services.responses.ChartMetric;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AccountScopeMoreAnalysisResponse {
    private String section;
    private List<ChartMetric> demographics = new ArrayList<>();
    private List<ChartMetric> geographics = new ArrayList<>();
    private List<ChartMetric> activities = new ArrayList<>();
    private List<ChartMetric> overview = new ArrayList<>();
    private List<ChartMetric> states = new ArrayList<>();
    private List<ChartMetric> categories = new ArrayList<>();
    private List<AccountCountrySectionResponse> countries = new ArrayList<>();
}