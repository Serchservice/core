package com.serch.server.services.company.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.company.TeamType;
import com.serch.server.services.company.responses.TeamGroupResponse;
import com.serch.server.services.company.responses.TeamResponse;

import java.util.List;

/**
 * This is the wrapper class that fetches team data from the database
 *
 * @see com.serch.server.services.company.services.implementations.TeamImplementation
 */
public interface TeamService {
    /**
     * Fetches the list of teams in groups
     *
     * @return ApiResponse of TeamGroupResponse in a list
     *
     * @see ApiResponse
     * @see TeamGroupResponse
     */
    ApiResponse<List<TeamGroupResponse>> teams();

    /**
     * Fetches the list of teams according to the teamType
     *
     * @param type The TeamType for the team to be fetched
     *
     * @return ApiResponse of TeamResponse
     *
     * @see ApiResponse
     * @see TeamResponse
     */
    ApiResponse<List<TeamResponse>> teams(TeamType type);
}