package com.serch.server.admin.services.scopes.account.services;

import com.serch.server.admin.services.scopes.account.responses.PlatformAccountScopeAnalysisResponse;
import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.auth.Role;

/**
 * Service interface for analyzing platform accounts based on various groupings and criteria.
 */
public interface PlatformAccountScopeService {
    /**
     * Fetches account analysis for the specified year, role, and guest access status.
     *
     * @param year     the year for the analysis
     * @param role     the role to filter the analysis by
     * @param forGuest indicates if the analysis is for guest access
     * @return an {@link ApiResponse} containing a {@link PlatformAccountScopeAnalysisResponse} with the account analysis data
     */
    ApiResponse<PlatformAccountScopeAnalysisResponse> fetchAccountAnalysis(Integer year, Role role, Boolean forGuest);

    /**
     * Fetches account analysis grouped by country for the specified year, role, and guest access status.
     *
     * @param year     the year for the analysis
     * @param role     the role to filter the analysis by
     * @param forGuest indicates if the analysis is for guest access
     * @return an {@link ApiResponse} containing a {@link PlatformAccountScopeAnalysisResponse} grouped by country
     */
    ApiResponse<PlatformAccountScopeAnalysisResponse> fetchGroupedAccountAnalysisByCountry(Integer year, Role role, Boolean forGuest);

    /**
     * Fetches account analysis grouped by timezone for the specified year, role, and guest access status.
     *
     * @param year     the year for the analysis
     * @param role     the role to filter the analysis by
     * @param forGuest indicates if the analysis is for guest access
     * @return an {@link ApiResponse} containing a {@link PlatformAccountScopeAnalysisResponse} grouped by timezone
     */
    ApiResponse<PlatformAccountScopeAnalysisResponse> fetchGroupedAccountAnalysisByTimezone(Integer year, Role role, Boolean forGuest);

    /**
     * Fetches account analysis grouped by state for the specified year, role, and guest access status.
     *
     * @param year     the year for the analysis
     * @param role     the role to filter the analysis by
     * @param forGuest indicates if the analysis is for guest access
     * @return an {@link ApiResponse} containing a {@link PlatformAccountScopeAnalysisResponse} grouped by state
     */
    ApiResponse<PlatformAccountScopeAnalysisResponse> fetchGroupedAccountAnalysisByState(Integer year, Role role, Boolean forGuest);

    /**
     * Fetches account analysis grouped by gender for the specified year, role, and guest access status.
     *
     * @param year     the year for the analysis
     * @param role     the role to filter the analysis by
     * @param forGuest indicates if the analysis is for guest access
     * @return an {@link ApiResponse} containing a {@link PlatformAccountScopeAnalysisResponse} grouped by gender
     */
    ApiResponse<PlatformAccountScopeAnalysisResponse> fetchGroupedAccountAnalysisByGender(Integer year, Role role, Boolean forGuest);

    /**
     * Fetches account analysis grouped by rating for the specified year, role, and guest access status.
     *
     * @param year     the year for the analysis
     * @param role     the role to filter the analysis by
     * @param forGuest indicates if the analysis is for guest access
     * @return an {@link ApiResponse} containing a {@link PlatformAccountScopeAnalysisResponse} grouped by rating
     */
    ApiResponse<PlatformAccountScopeAnalysisResponse> fetchGroupedAccountAnalysisByRating(Integer year, Role role, Boolean forGuest);

    /**
     * Fetches account analysis grouped by account status for the specified year and role.
     * Primarily used for roles such as {@link Role#PROVIDER}, {@link Role#ASSOCIATE_PROVIDER},
     * {@link Role#BUSINESS}, and {@link Role#USER}.
     *
     * @param year the year for the analysis
     * @param role the role to filter the analysis by
     * @return an {@link ApiResponse} containing a {@link PlatformAccountScopeAnalysisResponse} grouped by account status
     */
    ApiResponse<PlatformAccountScopeAnalysisResponse> fetchGroupedAccountAnalysisByAccountStatus(Integer year, Role role);

    /**
     * Fetches account analysis grouped by trip status for the specified year and role.
     * Primarily used for roles such as {@link Role#PROVIDER} and {@link Role#ASSOCIATE_PROVIDER}.
     *
     * @param year the year for the analysis
     * @param role the role to filter the analysis by
     * @return an {@link ApiResponse} containing a {@link PlatformAccountScopeAnalysisResponse} grouped by trip status
     */
    ApiResponse<PlatformAccountScopeAnalysisResponse> fetchGroupedAccountAnalysisByTripStatus(Integer year, Role role);

    /**
     * Fetches account analysis grouped by certification status for the specified year and role.
     * Primarily used for roles such as {@link Role#PROVIDER}, {@link Role#ASSOCIATE_PROVIDER},
     * and {@link Role#BUSINESS}.
     *
     * @param year the year for the analysis
     * @param role the role to filter the analysis by
     * @return an {@link ApiResponse} containing a {@link PlatformAccountScopeAnalysisResponse} grouped by certification status
     */
    ApiResponse<PlatformAccountScopeAnalysisResponse> fetchGroupedAccountAnalysisByCertified(Integer year, Role role);

    /**
     * Fetches account analysis grouped by category for the specified year and role.
     * Primarily used for roles such as {@link Role#PROVIDER}, {@link Role#ASSOCIATE_PROVIDER},
     * and {@link Role#BUSINESS}.
     *
     * @param year the year for the analysis
     * @param role the role to filter the analysis by
     * @return an {@link ApiResponse} containing a {@link PlatformAccountScopeAnalysisResponse} grouped by category
     */
    ApiResponse<PlatformAccountScopeAnalysisResponse> fetchGroupedAccountAnalysisByCategory(Integer year, Role role);
}