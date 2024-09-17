package com.serch.server.services.company.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.exceptions.others.CompanyException;
import com.serch.server.models.company.*;
import com.serch.server.repositories.company.LaunchedCountryRepository;
import com.serch.server.repositories.company.RequestCityRepository;
import com.serch.server.repositories.company.RequestedCountryRepository;
import com.serch.server.repositories.company.RequestedStateRepository;
import com.serch.server.services.company.requests.CountryRequest;
import com.serch.server.services.company.services.CompanyCountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation for managing country-related operations.
 * It implements its wrapper class {@link CompanyCountryService}
 *
 * @see LaunchedCountryRepository
 * @see RequestCityRepository
 * @see RequestedCountryRepository
 * @see RequestedStateRepository
 */
@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.NESTED)
public class CompanyCountryImplementation implements CompanyCountryService {
    private final LaunchedCountryRepository launchedCountryRepository;
    private final RequestedCountryRepository requestedCountryRepository;
    private final RequestedStateRepository requestedStateRepository;
    private final RequestCityRepository requestCityRepository;

    @Override
    public ApiResponse<String> checkMyLocation(CountryRequest request) {
        LaunchedCountry country = launchedCountryRepository.findByNameIgnoreCase(request.getCountry())
                .orElseThrow(() -> new CompanyException("Serch is not launched in %s".formatted(request.getCountry())));

        if(request.getCity() != null && request.getState() == null) {
            throw new CompanyException("Cannot get your state");
        } else if (request.getCity() != null) {
            return checkLaunchedCity(request, country);
        } else if (request.getState() != null) {
            return checkLaunchedState(request, country);
        } else {
            if(country.isNotActive()) {
                return new ApiResponse<>("%s is suspended at the moment".formatted(request.getCountry()));
            }
            return new ApiResponse<>(
                    "Serch is launched in %s".formatted(request.getCountry()),
                    HttpStatus.OK
            );
        }
    }

    private static ApiResponse<String> checkLaunchedState(CountryRequest request, LaunchedCountry country) {
        if(country.getLaunchedStates() == null || country.getLaunchedStates().isEmpty()) {
            return new ApiResponse<>(
                    "Serch is launched in %s".formatted(request.getState()),
                    HttpStatus.OK
            );
        } else {
            LaunchedState state = country.getLaunchedStates().stream()
                    .filter(c -> c.getName().contains(request.getState()))
                    .findFirst()
                    .orElse(new LaunchedState());

            if (state.isNotActive()) {
                return new ApiResponse<>("%s is suspended at the moment".formatted(request.getState()));
            } else {
                return new ApiResponse<>(
                        "Serch is launched in %s".formatted(request.getState()),
                        HttpStatus.OK
                );
            }
        }
    }

    private static ApiResponse<String> checkLaunchedCity(CountryRequest request, LaunchedCountry country) {
        LaunchedState state = country.getLaunchedStates().stream()
                .filter(s -> s.getName().contains(request.getState()))
                .findFirst()
                .orElse(new LaunchedState());

        if(country.getLaunchedStates() == null || country.getLaunchedStates().isEmpty() || state.getLaunchedCities() == null || state.getLaunchedCities().isEmpty()) {
            return new ApiResponse<>(
                    "Serch is launched in %s".formatted(request.getCity()),
                    HttpStatus.OK
            );
        } else if (state.getLaunchedCities().stream().noneMatch(c -> c.getName().contains(request.getCity()))) {
            throw new CompanyException("Serch is not launched in %s".formatted(request.getCity()));
        } else {
            LaunchedCity city = state.getLaunchedCities().stream()
                    .filter(c -> c.getName().contains(request.getCity()))
                    .findFirst()
                    .orElse(new LaunchedCity());

            if (city.isNotActive()) {
                return new ApiResponse<>("%s is suspended at the moment".formatted(request.getCity()));
            } else {
                return new ApiResponse<>(
                        "Serch is launched in %s".formatted(request.getCity()),
                        HttpStatus.OK
                );
            }
        }
    }

    @Override
    public ApiResponse<String> requestMyLocation(CountryRequest request) {
        if(request.getCity() != null && request.getState() == null) {
            throw new CompanyException("Cannot get your state");
        } else if (request.getCity() != null) {
            return requestCity(request);
        } else if (request.getState() != null) {
            return requestState(request);
        } else {
            if(requestedCountryRepository.existsByNameIgnoreCase(request.getCountry())) {
                throw new CompanyException("Country has already been requested for.");
            } else {
                RequestedCountry newCountry = new RequestedCountry();
                newCountry.setName(request.getCountry());
                requestedCountryRepository.save(newCountry);
            }
            return new ApiResponse<>("Country request processed successfully.", HttpStatus.CREATED);
        }
    }

    private ApiResponse<String> requestState(CountryRequest request) {
        RequestedCountry country = requestedCountryRepository.findByNameIgnoreCase(request.getCountry())
                .orElseGet(() -> {
                    RequestedCountry newCountry = new RequestedCountry();
                    newCountry.setName(request.getCountry());
                    requestedCountryRepository.save(newCountry);
                    return newCountry;
                });
        if (country.getRequestedStates() != null && country.getRequestedStates().stream().anyMatch(c -> c.getName().contains(request.getState()))) {
            throw new CompanyException("State has already been requested for.");
        }
        RequestedState state = new RequestedState();
        state.setRequestedCountry(country);
        state.setName(request.getState());
        requestedStateRepository.save(state);
        return new ApiResponse<>("State request processed successfully.", HttpStatus.CREATED);
    }

    private ApiResponse<String> requestCity(CountryRequest request) {
        RequestedCountry country = requestedCountryRepository.findByNameIgnoreCase(request.getCountry())
                .orElseGet(() -> {
                    RequestedCountry newCountry = new RequestedCountry();
                    newCountry.setName(request.getCountry());
                    requestedCountryRepository.save(newCountry);
                    return newCountry;
                });
        RequestedState state = getState(country, request);

        if (state.getRequestedCities() != null && state.getRequestedCities().stream().anyMatch(c -> c.getName().contains(request.getCity()))) {
            throw new CompanyException("City has already been requested for.");
        }

        RequestedCity requestedCity = new RequestedCity();
        requestedCity.setRequestedState(state);
        requestedCity.setName(request.getCity());
        requestCityRepository.save(requestedCity);

        return new ApiResponse<>("City request processed successfully.", HttpStatus.CREATED);
    }

    private RequestedState getState(RequestedCountry country, CountryRequest request) {
        if(country.getRequestedStates() != null) {
            return country.getRequestedStates().stream()
                    .filter(s -> s.getName().contains(request.getState()))
                    .findFirst()
                    .orElseGet(() -> {
                        RequestedState newState = new RequestedState();
                        newState.setRequestedCountry(country);
                        newState.setName(request.getState());
                        return requestedStateRepository.save(newState);
                    });
        } else {
            RequestedState newState = new RequestedState();
            newState.setRequestedCountry(country);
            newState.setName(request.getState());
            return requestedStateRepository.save(newState);
        }
    }
}