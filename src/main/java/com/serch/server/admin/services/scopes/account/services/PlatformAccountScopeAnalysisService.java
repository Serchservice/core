package com.serch.server.admin.services.scopes.account.services;

import com.serch.server.admin.services.responses.ChartMetric;
import com.serch.server.admin.services.scopes.account.responses.PlatformAccountScopeMoreAnalysisResponse;
import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.auth.Role;

import java.util.List;
import java.util.Map;

/**
 * Service interface for fetching analysis data for platform accounts within a specified scope.
 * Provides methods to retrieve detailed analysis data based on user roles and, optionally, a specified year.
 * This interface supports fetching metrics related to demographics, business categories,
 * profile categories, and other analysis data relevant to platform accounts.
 */
public interface PlatformAccountScopeAnalysisService {

    /**
     * Fetches analysis data for a specified role.
     *
     * @param role The {@link Role} for which analysis data is to be fetched.
     *             This parameter determines the user category or account type whose analysis data
     *             is required, such as ADMIN, USER, or BUSINESS roles.
     * @return An {@link ApiResponse} containing a {@link PlatformAccountScopeMoreAnalysisResponse} object
     *         with aggregated analysis data relevant to the specified role.
     */
    ApiResponse<PlatformAccountScopeMoreAnalysisResponse> fetch(Role role);

    /**
     * Fetches analysis data for a specified role and year.
     *
     * @param role The {@link Role} for which analysis data is to be fetched.
     *             The role determines the user category or account type to be analyzed.
     * @param year The {@link Integer} representing the year for which the analysis data is to be fetched.
     *             This parameter restricts the data to the specified year, allowing for time-based comparisons.
     * @return An {@link ApiResponse} containing a list of {@link PlatformAccountScopeMoreAnalysisResponse}
     *         objects, with each entry representing the analysis data for the specified role within the year.
     */
    ApiResponse<List<PlatformAccountScopeMoreAnalysisResponse>> fetch(Role role, Integer year);

    /**
     * Retrieves the count of users categorized by gender.
     *
     * @return A {@link Map} where the key is a {@link String} representing a gender type
     *         (e.g., "Male", "Female"), and the value is a {@link Long} representing the count of users
     *         within that gender group.
     */
    Map<String, Long> getGender();

    /**
     * Constructs a chart metric based on the given parameters.
     *
     * @param count A {@link Long} representing the count of entities associated with this metric.
     * @param id    A {@link String} identifier for the metric, typically representing a specific category.
     * @param info  A {@link String} providing additional details or context about the metric.
     * @return A {@link ChartMetric} object populated with the provided count, id, and info, representing
     *         a single data point on an analysis chart.
     */
    ChartMetric getMetric(Long count, String id, String info);

    /**
     * Retrieves the count of accounts categorized by business type.
     *
     * @return A {@link Map} where the key is a {@link String} representing a business category
     *         (e.g., "Retail", "Service"), and the value is a {@link Long} representing the count
     *         of accounts in that business category.
     */
    Map<String, Long> getBusinessCategory();

    /**
     * Retrieves the count of profiles categorized by type.
     *
     * @return A {@link Map} where the key is a {@link String} representing a profile category
     *         (e.g., "Standard", "Premium"), and the value is a {@link Long} representing the count
     *         of profiles in that category.
     */
    Map<String, Long> getProfileCategory();
}