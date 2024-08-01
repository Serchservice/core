package com.serch.server.admin.services.scopes.countries_in_serch;

import com.serch.server.admin.services.responses.ChartMetric;
import com.serch.server.admin.services.scopes.countries_in_serch.requests.AddCountryRequest;
import com.serch.server.admin.services.scopes.countries_in_serch.requests.AddStateRequest;
import com.serch.server.admin.services.scopes.countries_in_serch.responses.CountriesInSerchResponse;
import com.serch.server.admin.services.scopes.countries_in_serch.responses.CountryInSerchResponse;
import com.serch.server.bases.ApiResponse;

import java.util.List;

public interface CountriesInSerchService {
    /**
     * Get an overview of Serch in the requested and launched country views.
     *
     * @see com.serch.server.models.company.LaunchedCountry
     * @see com.serch.server.models.company.RequestedCountry
     * @see com.serch.server.models.company.LaunchedState
     * @see com.serch.server.models.company.RequestedState
     *
     * @return {@link ApiResponse} of {@link CountriesInSerchResponse}
     */
    ApiResponse<CountriesInSerchResponse> overview();

    /**
     * Get a list of launched countries in Serch.
     *
     * @see com.serch.server.models.company.LaunchedCountry
     *
     * @return {@link ApiResponse} list of {@link CountryInSerchResponse}
     */
    ApiResponse<List<CountryInSerchResponse>> launchedCountries();

    /**
     * Get a list of requested countries in Serch
     *
     * @see com.serch.server.models.company.RequestedCountry
     *
     * @return {@link ApiResponse} list of {@link CountryInSerchResponse}
     */
    ApiResponse<List<CountryInSerchResponse>> requestedCountries();

    /**
     * Add a new country to {@link com.serch.server.models.company.LaunchedCountry}, marking it launched.
     * This returns the new updated list of launched countries.
     *
     * @param request The {@link AddCountryRequest} data for a new country information
     *
     * @return {@link ApiResponse} list of {@link CountryInSerchResponse}
     */
    ApiResponse<List<CountryInSerchResponse>> add(AddCountryRequest request);

    /**
     * Add a new state to {@link com.serch.server.models.company.LaunchedCountry}, to create a new
     * {@link com.serch.server.models.company.LaunchedState} data.
     * <p></p>
     * This returns the new updated {@link CountryInSerchResponse}
     *
     * @param request The {@link AddStateRequest} data for a new state
     *
     * @return {@link ApiResponse} of updated {@link CountryInSerchResponse}
     */
    ApiResponse<CountryInSerchResponse> add(AddStateRequest request);

    /**
     * Fetch the chart data for launched country in Serch
     *
     * @param year The year being requested in {@link Integer}
     *
     * @return {@link ApiResponse} list of {@link ChartMetric}
     */
    ApiResponse<List<ChartMetric>> launchedCountryChart(Integer year);

    /**
     * Fetch the chart data for requested country in Serch
     *
     * @param year The year being requested in {@link Integer}
     *
     * @return {@link ApiResponse} list of {@link ChartMetric}
     */
    ApiResponse<List<ChartMetric>> requestedCountryChart(Integer year);

    /**
     * Fetch the chart data for launched state in Serch
     *
     * @param year The year being requested in {@link Integer}
     *
     * @return {@link ApiResponse} list of {@link ChartMetric}
     */
    ApiResponse<List<ChartMetric>> launchedStateChart(Integer year);

    /**
     * Fetch the chart data for requested state in Serch
     *
     * @param year The year being requested in {@link Integer}
     *
     * @return {@link ApiResponse} list of {@link ChartMetric}
     */
    ApiResponse<List<ChartMetric>> requestedStateChart(Integer year);

    /**
     * Mark country as {@link com.serch.server.enums.account.AccountStatus#ACTIVE} or
     * {@link com.serch.server.enums.account.AccountStatus#SUSPENDED}
     *
     * @param id The {@link com.serch.server.models.company.LaunchedCountry} id for update
     *
     * @return {@link ApiResponse} of updated {@link CountryInSerchResponse}
     */
    ApiResponse<CountryInSerchResponse> toggleStatus(Long id);

    /**
     * Deletes a {@link com.serch.server.models.company.LaunchedCountry} from the table
     *
     * @param id The {@link com.serch.server.models.company.LaunchedCountry} id to be deleted
     *
     * @return {@link ApiResponse} updated list of {@link CountryInSerchResponse}
     */
    ApiResponse<List<CountryInSerchResponse>> deleteLaunchedCountry(Long id);

    /**
     * Delete a state from the {@link com.serch.server.models.company.LaunchedState} table
     *
     * @param id The {@link com.serch.server.models.company.LaunchedState} id to be deleted
     *
     * @return {@link ApiResponse} of updated {@link CountryInSerchResponse}
     */
    ApiResponse<CountryInSerchResponse> deleteLaunched(Long id);

    /**
     * Deletes a {@link com.serch.server.models.company.RequestedCountry} from the table
     *
     * @param id The {@link com.serch.server.models.company.RequestedCountry} id to be deleted
     *
     * @return {@link ApiResponse} updated list of {@link CountryInSerchResponse}
     */
    ApiResponse<List<CountryInSerchResponse>> deleteRequestedCountry(Long id);

    /**
     * Delete a state from the {@link com.serch.server.models.company.RequestedState} table
     *
     * @param id The {@link com.serch.server.models.company.RequestedState} id to be deleted
     *
     * @return {@link ApiResponse} of updated {@link CountryInSerchResponse}
     */
    ApiResponse<CountryInSerchResponse> deleteRequested(Long id);
}
