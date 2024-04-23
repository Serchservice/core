package com.serch.server.services.company.responses;

import com.serch.server.enums.company.TeamType;
import lombok.Data;

@Data
public class TeamResponse {
    private String name;
    private String about;
    private String image;
    private String link;
    private String position;
    private TeamType team;
    private String type;
    private Integer hierarchy = 0;
}