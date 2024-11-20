package com.serch.server.admin.services.team.responses;

import com.serch.server.admin.services.responses.Metric;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TeamOverviewResponse {
    private List<Metric> overview = new ArrayList<>();
    private List<Metric> teams = new ArrayList<>();
    private List<AdminActivityResponse> activities = new ArrayList<>();
    private CompanyStructure structure;
}
