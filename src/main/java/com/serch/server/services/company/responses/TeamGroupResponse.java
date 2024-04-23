package com.serch.server.services.company.responses;

import com.serch.server.enums.company.TeamType;
import lombok.Data;

import java.util.List;

@Data
public class TeamGroupResponse {
    private TeamType team;
    private String type;
    private List<TeamResponse> teams;
}
