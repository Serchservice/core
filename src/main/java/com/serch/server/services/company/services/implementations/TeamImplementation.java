package com.serch.server.services.company.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.company.TeamType;
import com.serch.server.mappers.CompanyMapper;
import com.serch.server.models.company.Team;
import com.serch.server.repositories.company.TeamRepository;
import com.serch.server.services.company.responses.TeamGroupResponse;
import com.serch.server.services.company.responses.TeamResponse;
import com.serch.server.services.company.services.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This is the implementation of the {@link TeamService} wrapper class.
 *
 * @see TeamRepository
 */
@Service
@RequiredArgsConstructor
public class TeamImplementation implements TeamService {
    private final TeamRepository teamRepository;

    @Override
    public ApiResponse<List<TeamGroupResponse>> teams() {
        Map<TeamType, List<TeamResponse>> teamGroups = teamRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Team::getHierarchy))
                .map(team -> {
                    TeamResponse response = CompanyMapper.INSTANCE.response(team);
                    response.setType(team.getTeam().getType());
                    return response;
                })
                .collect(Collectors.groupingBy(TeamResponse::getTeam));

        List<TeamGroupResponse> response = new ArrayList<>();
        teamGroups.forEach((team, teams) -> {
            TeamGroupResponse group = new TeamGroupResponse();
            group.setTeam(team);
            group.setTeams(teams);
            group.setType(team.getType());
            response.add(group);
        });
        return new ApiResponse<>(response);
    }

    @Override
    public ApiResponse<List<TeamResponse>> teams(TeamType type) {
        List<TeamResponse> teams = teamRepository.findByTeam(type)
                .stream()
                .sorted(Comparator.comparing(Team::getHierarchy))
                .map(team -> {
                    TeamResponse response = CompanyMapper.INSTANCE.response(team);
                    response.setType(team.getTeam().getType());
                    return response;
                })
                .toList();

        return new ApiResponse<>(teams);
    }
}