package com.serch.server.admin.services.scopes.account.services;

import com.serch.server.admin.services.scopes.account.responses.AccountScopeAnalysisResponse;
import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.auth.Role;

/**
 * Service interface for analyzing platform accounts based on various groupings and criteria.
 */
public interface AccountScopeAnalysisService {
    /**
     * Fetches account analysis for the specified year, role, and guest access status.
     *
     * @param year     the year for the analysis
     * @param role     the role to filter the analysis by
     * @param forGuest indicates if the analysis is for guest access
     * @return an {@link ApiResponse} containing a {@link AccountScopeAnalysisResponse} with the account analysis response
     */
    ApiResponse<AccountScopeAnalysisResponse> fetchAccountAnalysis(Integer year, Role role, Boolean forGuest);

    /**
     * Fetches account analysis grouped by country for the specified year, role, and guest access status.
     *
     * @param year     the year for the analysis
     * @param role     the role to filter the analysis by
     * @param forGuest indicates if the analysis is for guest access
     * @return an {@link ApiResponse} containing a {@link AccountScopeAnalysisResponse} grouped by country
     */
    ApiResponse<AccountScopeAnalysisResponse> fetchGroupedAccountAnalysisByCountry(Integer year, Role role, Boolean forGuest);

    /**
     * Fetches account analysis grouped by timezone for the specified year, role, and guest access status.
     *
     * @param year     the year for the analysis
     * @param role     the role to filter the analysis by
     * @param forGuest indicates if the analysis is for guest access
     * @return an {@link ApiResponse} containing a {@link AccountScopeAnalysisResponse} grouped by timezone
     */
    ApiResponse<AccountScopeAnalysisResponse> fetchGroupedAccountAnalysisByTimezone(Integer year, Role role, Boolean forGuest);

    /**
     * Fetches account analysis grouped by state for the specified year, role, and guest access status.
     *
     * @param year     the year for the analysis
     * @param role     the role to filter the analysis by
     * @param forGuest indicates if the analysis is for guest access
     * @return an {@link ApiResponse} containing a {@link AccountScopeAnalysisResponse} grouped by state
     */
    ApiResponse<AccountScopeAnalysisResponse> fetchGroupedAccountAnalysisByState(Integer year, Role role, Boolean forGuest);

    /**
     * Fetches account analysis grouped by gender for the specified year, role, and guest access status.
     *
     * @param year     the year for the analysis
     * @param role     the role to filter the analysis by
     * @param forGuest indicates if the analysis is for guest access
     * @return an {@link ApiResponse} containing a {@link AccountScopeAnalysisResponse} grouped by gender
     */
    ApiResponse<AccountScopeAnalysisResponse> fetchGroupedAccountAnalysisByGender(Integer year, Role role, Boolean forGuest);

    /**
     * Fetches account analysis grouped by rating for the specified year, role, and guest access status.
     *
     * @param year     the year for the analysis
     * @param role     the role to filter the analysis by
     * @param forGuest indicates if the analysis is for guest access
     * @return an {@link ApiResponse} containing a {@link AccountScopeAnalysisResponse} grouped by rating
     */
    ApiResponse<AccountScopeAnalysisResponse> fetchGroupedAccountAnalysisByRating(Integer year, Role role, Boolean forGuest);

    /**
     * Fetches account analysis grouped by account status for the specified year and role.
     * Primarily used for roles such as {@link Role#PROVIDER}, {@link Role#ASSOCIATE_PROVIDER},
     * {@link Role#BUSINESS}, and {@link Role#USER}.
     *
     * @param year the year for the analysis
     * @param role the role to filter the analysis by
     * @return an {@link ApiResponse} containing a {@link AccountScopeAnalysisResponse} grouped by account status
     */
    ApiResponse<AccountScopeAnalysisResponse> fetchGroupedAccountAnalysisByAccountStatus(Integer year, Role role);

    /**
     * Fetches account analysis grouped by trip status for the specified year and role.
     * Primarily used for roles such as {@link Role#PROVIDER} and {@link Role#ASSOCIATE_PROVIDER}.
     *
     * @param year the year for the analysis
     * @param role the role to filter the analysis by
     * @return an {@link ApiResponse} containing a {@link AccountScopeAnalysisResponse} grouped by trip status
     */
    ApiResponse<AccountScopeAnalysisResponse> fetchGroupedAccountAnalysisByTripStatus(Integer year, Role role);

    /**
     * Fetches account analysis grouped by certification status for the specified year and role.
     * Primarily used for roles such as {@link Role#PROVIDER}, {@link Role#ASSOCIATE_PROVIDER},
     * and {@link Role#BUSINESS}.
     *
     * @param year the year for the analysis
     * @param role the role to filter the analysis by
     * @return an {@link ApiResponse} containing a {@link AccountScopeAnalysisResponse} grouped by certification status
     */
    ApiResponse<AccountScopeAnalysisResponse> fetchGroupedAccountAnalysisByCertified(Integer year, Role role);

    /**
     * Fetches account analysis grouped by category for the specified year and role.
     * Primarily used for roles such as {@link Role#PROVIDER}, {@link Role#ASSOCIATE_PROVIDER},
     * and {@link Role#BUSINESS}.
     *
     * @param year the year for the analysis
     * @param role the role to filter the analysis by
     * @return an {@link ApiResponse} containing a {@link AccountScopeAnalysisResponse} grouped by category
     */
    ApiResponse<AccountScopeAnalysisResponse> fetchGroupedAccountAnalysisByCategory(Integer year, Role role);
}