package com.serch.server.admin.services.scopes.features.services;

import com.serch.server.admin.services.responses.ChartMetric;
import com.serch.server.admin.services.scopes.features.responses.ShopScopeOverviewResponse;
import com.serch.server.admin.services.scopes.features.responses.ShopScopeResponse;
import com.serch.server.bases.ApiResponse;

import java.util.List;

/**
 * Service interface for managing shop-related features on the Serch platform.
 * Provides methods to retrieve overviews, chart response, and shop details, as well as search for individual shops by ID.
 */
public interface ShopScopeService {

    /**
     * Retrieves an overview of shop-related activities on the Serch platform.
     * This overview may include metrics and insights about shops, such as the number of shops,
     * activity levels, and other key performance indicators.
     *
     * @return an {@link ApiResponse} containing a {@link ShopScopeOverviewResponse}
     *         with aggregated response and insights related to shops on the platform.
     */
    ApiResponse<ShopScopeOverviewResponse> overview();

    /**
     * Retrieves chart response for shops on the Serch platform for a specified year.
     * Provides insights into trends and metrics for shops, such as sales growth, user engagement,
     * and other performance indicators over the specified time period.
     *
     * @param year the year for which to fetch the response, represented as an {@link Integer}.
     *             If the year is null, response for the current year will be used.
     * @return an {@link ApiResponse} containing a list of {@link ChartMetric} objects
     *         representing the shop-related response, suitable for trend visualization.
     */
    ApiResponse<List<ChartMetric>> chart(Integer year);

    /**
     * Retrieves a list of shops on the Serch platform.
     * The list includes details about each shop, such as name, location, owner information,
     * categories, and other relevant attributes.
     *
     * @return an {@link ApiResponse} containing a list of {@link ShopScopeResponse}
     *         objects representing individual shops and their associated details.
     */
    ApiResponse<List<ShopScopeResponse>> list();

    /**
     * Retrieves details of a specific shop by its unique identifier on the Serch platform.
     * Provides information about the shop, such as its name, description, location, owner, and other relevant details.
     *
     * @param id the unique identifier of the shop to be fetched, represented as a {@link String}.
     * @return an {@link ApiResponse} containing a {@link ShopScopeResponse}
     *         representing the details of the specified shop.
     */
    ApiResponse<ShopScopeResponse> find(String id);
}