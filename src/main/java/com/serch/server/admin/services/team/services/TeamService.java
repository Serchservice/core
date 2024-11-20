package com.serch.server.admin.services.team.services;

import com.serch.server.admin.services.team.responses.AdminListResponse;
import com.serch.server.admin.services.team.responses.TeamOverviewResponse;
import com.serch.server.admin.services.team.responses.CompanyStructure;
import com.serch.server.bases.ApiResponse;

/**
 * Service interface for managing team-related operations within the Serch organization.
 * This interface provides methods to retrieve team overviews, company structures,
 * and lists of admins. Implementations of this interface handle the business logic
 * for accessing and manipulating team-related data.
 */
public interface TeamService {

    /**
     * Retrieves an overview of the Serch teams, which includes relevant information
     * such as team names, members, performance metrics, and any other pertinent data
     * that provides insights into team operations and structure.
     *
     * @return {@link ApiResponse} containing a {@link TeamOverviewResponse} object
     * that holds the detailed overview of the teams.
     */
    ApiResponse<TeamOverviewResponse> overview();

    /**
     * Retrieves the organizational structure of the Serch company specifically
     * for the admins. This structure outlines the hierarchy and relationships
     * between different teams and roles within the organization,
     * allowing for better understanding and navigation of the company.
     *
     * @return {@link CompanyStructure} representing the hierarchical structure
     * of the company, including teams, departments, and reporting lines.
     */
    CompanyStructure team();

    /**
     * Retrieves a comprehensive list of admins within the organization,
     * detailing their roles, responsibilities, statuses, and any other relevant
     * information. This information can be used for managing admin operations
     * and ensuring appropriate resource allocation.
     *
     * @return {@link ApiResponse} containing an {@link AdminListResponse} object
     * that holds a list of admins and their associated data.
     */
    ApiResponse<AdminListResponse> admins();
}