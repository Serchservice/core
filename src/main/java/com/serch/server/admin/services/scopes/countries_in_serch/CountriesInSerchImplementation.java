package com.serch.server.admin.services.scopes.countries_in_serch;

import com.serch.server.admin.mappers.AdminCompanyMapper;
import com.serch.server.admin.services.responses.ChartMetric;
import com.serch.server.admin.services.responses.Metric;
import com.serch.server.admin.services.scopes.countries_in_serch.requests.AddCountryRequest;
import com.serch.server.admin.services.scopes.countries_in_serch.requests.AddStateRequest;
import com.serch.server.admin.services.scopes.countries_in_serch.responses.CountriesInSerchResponse;
import com.serch.server.admin.services.scopes.countries_in_serch.responses.CountryInSerchResponse;
import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.account.AccountStatus;
import com.serch.server.enums.auth.Role;
import com.serch.server.exceptions.others.SerchException;
import com.serch.server.models.auth.User;
import com.serch.server.models.company.LaunchedCountry;
import com.serch.server.models.company.LaunchedState;
import com.serch.server.models.company.RequestedCountry;
import com.serch.server.models.company.RequestedState;
import com.serch.server.models.shared.Guest;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.repositories.company.LaunchedCountryRepository;
import com.serch.server.repositories.company.LaunchedStateRepository;
import com.serch.server.repositories.company.RequestedCountryRepository;
import com.serch.server.repositories.company.RequestedStateRepository;
import com.serch.server.repositories.shared.GuestRepository;
import com.serch.server.utils.AdminUtil;
import com.serch.server.utils.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CountriesInSerchImplementation implements CountriesInSerchService {
    private final RequestedStateRepository requestedStateRepository;
    private final RequestedCountryRepository requestedCountryRepository;
    private final LaunchedStateRepository launchedStateRepository;
    private final LaunchedCountryRepository launchedCountryRepository;
    private final UserRepository userRepository;
    private final GuestRepository guestRepository;

    @Override
    @Transactional
    public ApiResponse<CountriesInSerchResponse> overview() {
        CountriesInSerchResponse response = new CountriesInSerchResponse();

        List<Metric> launchedMetrics = new ArrayList<>();
        List<LaunchedCountry> launchedCountries = launchedCountryRepository.findAll();
        launchedCountries.forEach(launchedCountry -> {
            Metric metric = new Metric();
            metric.setHeader(launchedCountry.getName());
            metric.setCount(String.valueOf(launchedCountry.getLaunchedStates().size()));
            metric.setFeature(launchedCountry.getEmoji());
            launchedMetrics.add(metric);
        });
        response.setLaunchedMetrics(launchedMetrics);

        List<Metric> requestedMetrics = new ArrayList<>();
        List<RequestedCountry> requestedCountries = requestedCountryRepository.findAll();
        requestedCountries.forEach(requestedCountry -> {
            Metric metric = new Metric();
            metric.setHeader(requestedCountry.getName());
            metric.setCount(String.valueOf(requestedCountry.getRequestedStates().size()));
            requestedMetrics.add(metric);
        });
        response.setRequestedMetrics(requestedMetrics);

        ApiResponse<List<ChartMetric>> launchedCountryChart = launchedCountryChart(AdminUtil.currentYear());
        ApiResponse<List<ChartMetric>> launchedStateChart = launchedStateChart(AdminUtil.currentYear());
        ApiResponse<List<ChartMetric>> requestedCountryChart = requestedCountryChart(AdminUtil.currentYear());
        ApiResponse<List<ChartMetric>> requestedStateChart = requestedStateChart(AdminUtil.currentYear());

        response.setLaunchedCountryChart(launchedCountryChart.getData());
        response.setLaunchedStateChart(launchedStateChart.getData());
        response.setRequestedCountryChart(requestedCountryChart.getData());
        response.setRequestedStateChart(requestedStateChart.getData());
        response.setYears(AdminUtil.years());

        return new ApiResponse<>(response);
    }

    @Override
    @Transactional
    public ApiResponse<List<ChartMetric>> launchedCountryChart(Integer year) {
        List<ChartMetric> chartMetrics = new ArrayList<>();
        List<LaunchedCountry> launchedCountries = launchedCountryRepository.findAll();

        for (LaunchedCountry launchedCountry : launchedCountries) {
            ChartMetric metric = new ChartMetric();
            metric.setLabel(launchedCountry.getName() + " - " + launchedCountry.getEmoji());

            List<User> users = userRepository.findByCountryLikeIgnoreCase(launchedCountry.getName());
            List<Guest> guests = guestRepository.findByCountryLikeIgnoreCase(launchedCountry.getName());

            buildChartMetric(chartMetrics, metric, users, guests);
        }

        return new ApiResponse<>(chartMetrics);
    }

    private void buildChartMetric(List<ChartMetric> chartMetrics, ChartMetric metric, List<User> users, List<Guest> guests) {
        metric.setUsers(users.stream().filter(user -> user.getRole() == Role.USER).toList().size());
        metric.setProviders(users.stream().filter(user -> user.getRole() == Role.PROVIDER).toList().size());
        metric.setAssociates(users.stream().filter(user -> user.getRole() == Role.ASSOCIATE_PROVIDER).toList().size());
        metric.setBusinesses(users.stream().filter(user -> user.getRole() == Role.BUSINESS).toList().size());
        metric.setGuests(guests.size());

        chartMetrics.add(metric);
    }

    @Override
    @Transactional
    public ApiResponse<List<ChartMetric>> launchedStateChart(Integer year) {
        List<ChartMetric> chartMetrics = new ArrayList<>();
        List<LaunchedState> launchedStates = launchedStateRepository.findAll();

        for (LaunchedState launchedState : launchedStates) {
            ChartMetric metric = new ChartMetric();
            metric.setLabel(launchedState.getName() + " (" + launchedState.getLaunchedCountry().getEmoji() + ")");

            List<User> users = userRepository.findByStateLikeIgnoreCase(launchedState.getName());
            List<Guest> guests = guestRepository.findByStateLikeIgnoreCase(launchedState.getName());

            buildChartMetric(chartMetrics, metric, users, guests);
        }

        return new ApiResponse<>(chartMetrics);
    }

    @Override
    @Transactional
    public ApiResponse<List<ChartMetric>> requestedCountryChart(Integer year) {
        List<ChartMetric> chartMetrics = new ArrayList<>();
        List<RequestedCountry> requestedCountries = requestedCountryRepository.findAll();

        for (RequestedCountry requestedCountry : requestedCountries) {
            ChartMetric metric = new ChartMetric();
            metric.setLabel(requestedCountry.getName());

            List<User> users = userRepository.findByCountryLikeIgnoreCase(requestedCountry.getName());
            List<Guest> guests = guestRepository.findByCountryLikeIgnoreCase(requestedCountry.getName());

            buildChartMetric(chartMetrics, metric, users, guests);
        }

        return new ApiResponse<>(chartMetrics);
    }

    @Override
    @Transactional
    public ApiResponse<List<ChartMetric>> requestedStateChart(Integer year) {
        List<ChartMetric> chartMetrics = new ArrayList<>();
        List<RequestedState> requestedStates = requestedStateRepository.findAll();

        for (RequestedState requestedState : requestedStates) {
            ChartMetric metric = new ChartMetric();
            metric.setLabel(requestedState.getName() + " (" + requestedState.getRequestedCountry().getName() + ")");

            List<User> users = userRepository.findByStateLikeIgnoreCase(requestedState.getName());
            List<Guest> guests = guestRepository.findByStateLikeIgnoreCase(requestedState.getName());

            buildChartMetric(chartMetrics, metric, users, guests);
        }

        return new ApiResponse<>(chartMetrics);
    }

    @Override
    @Transactional
    public ApiResponse<List<CountryInSerchResponse>> launchedCountries() {
        List<CountryInSerchResponse> response = launchedCountryRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(LaunchedCountry::getCreatedAt).reversed())
                .map(this::launched)
                .toList();
        return new ApiResponse<>(response);
    }

    @Override
    @Transactional
    public ApiResponse<List<CountryInSerchResponse>> requestedCountries() {
        List<CountryInSerchResponse> response = requestedCountryRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(RequestedCountry::getCreatedAt).reversed())
                .map(this::requested)
                .toList();
        return new ApiResponse<>(response);
    }

    @Override
    @Transactional
    public ApiResponse<List<CountryInSerchResponse>> add(AddCountryRequest request) {
        LaunchedCountry country = AdminCompanyMapper.instance.country(request);
        launchedCountryRepository.save(country);
        List<CountryInSerchResponse> response = launchedCountryRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(LaunchedCountry::getCreatedAt).reversed())
                .map(this::launched)
                .toList();
        return new ApiResponse<>(
                "Yay! Serch is now launched in %s".formatted(country.getName()),
                response,
                HttpStatus.OK
        );
    }

    @Override
    @Transactional
    public ApiResponse<CountryInSerchResponse> add(AddStateRequest request) {
        LaunchedCountry country = launchedCountryRepository.findById(request.getId())
                .orElseThrow(() -> new SerchException("Country not found"));
        LaunchedState state = new LaunchedState();
        state.setName(request.getState());
        state.setLaunchedCountry(country);
        LaunchedState savedState = launchedStateRepository.save(state);

        country.getLaunchedStates().add(savedState);
        return new ApiResponse<>(
                "Yay! Serch is now launched in (%s)%s - %s state".formatted(country.getEmoji(), country.getName(), state.getName()),
                launched(country),
                HttpStatus.OK
        );
    }

    @Override
    @Transactional
    public ApiResponse<CountryInSerchResponse> toggleStatus(Long id) {
        LaunchedCountry country = launchedCountryRepository.findById(id)
                .orElseThrow(() -> new SerchException("Country not found"));
        if(country.getStatus() == AccountStatus.SUSPENDED) {
            country.setStatus(AccountStatus.ACTIVE);
        } else {
            country.setStatus(AccountStatus.SUSPENDED);
        }
        country.setUpdatedAt(TimeUtil.now());
        launchedCountryRepository.save(country);
        return new ApiResponse<>(
                "%s is now %s".formatted(country.getName(), country.getStatus().getType().toLowerCase()),
                launched(country),
                HttpStatus.OK
        );
    }

    @Override
    @Transactional
    public ApiResponse<List<CountryInSerchResponse>> deleteLaunchedCountry(Long id) {
        LaunchedCountry country = launchedCountryRepository.findById(id)
                .orElseThrow(() -> new SerchException("Country not found"));
        launchedCountryRepository.delete(country);
        List<CountryInSerchResponse> response = launchedCountryRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(LaunchedCountry::getCreatedAt).reversed())
                .map(this::launched)
                .toList();
        return new ApiResponse<>(
                "%s deleted successfully".formatted(country.getName()),
                response,
                HttpStatus.OK
        );
    }

    @Override
    @Transactional
    public ApiResponse<CountryInSerchResponse> deleteLaunched(Long id) {
        LaunchedState state = launchedStateRepository.findById(id)
                .orElseThrow(() -> new SerchException("State not found"));
        LaunchedCountry country = state.getLaunchedCountry();
        country.getLaunchedStates().remove(state);
        launchedStateRepository.delete(state);
        return new ApiResponse<>(
                "%s deleted successfully".formatted(state.getName()),
                launched(country),
                HttpStatus.OK
        );
    }

    private CountryInSerchResponse launched(LaunchedCountry country) {
        CountryInSerchResponse response = AdminCompanyMapper.instance.response(country);

        if(country.getLaunchedStates() != null && !country.getLaunchedStates().isEmpty()) {
            response.setStates(country.getLaunchedStates().stream()
                    .sorted(Comparator.comparing(LaunchedState::getCreatedAt).reversed())
                    .map(AdminCompanyMapper.instance::response).toList());
        }
        return response;
    }

    @Override
    @Transactional
    public ApiResponse<List<CountryInSerchResponse>> deleteRequestedCountry(Long id) {
        RequestedCountry country = requestedCountryRepository.findById(id)
                .orElseThrow(() -> new SerchException("Country not found"));
        requestedCountryRepository.delete(country);
        List<CountryInSerchResponse> response = requestedCountryRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(RequestedCountry::getCreatedAt).reversed())
                .map(this::requested)
                .toList();
        return new ApiResponse<>(
                "%s deleted successfully".formatted(country.getName()),
                response,
                HttpStatus.OK
        );
    }

    @Override
    @Transactional
    public ApiResponse<CountryInSerchResponse> deleteRequested(Long id) {
        RequestedState state = requestedStateRepository.findById(id)
                .orElseThrow(() -> new SerchException("State not found"));
        RequestedCountry country = state.getRequestedCountry();
        country.getRequestedStates().remove(state);
        requestedStateRepository.delete(state);
        return new ApiResponse<>(
                "%s deleted successfully".formatted(state.getName()),
                requested(country),
                HttpStatus.OK
        );
    }

    private CountryInSerchResponse requested(RequestedCountry country) {
        CountryInSerchResponse response = AdminCompanyMapper.instance.response(country);

        if(country.getRequestedStates() != null && !country.getRequestedStates().isEmpty()) {
            response.setStates(country.getRequestedStates().stream()
                    .sorted(Comparator.comparing(RequestedState::getCreatedAt).reversed())
                    .map(AdminCompanyMapper.instance::response).toList());
        }
        return response;
    }
}
