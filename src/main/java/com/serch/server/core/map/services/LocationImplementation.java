package com.serch.server.core.map.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.core.map.responses.Address;
import com.serch.server.core.map.responses.LocationIQPlace;
import com.serch.server.mappers.CoreMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Implementation of the location-related services.
 *
 * @see LocationService
 * @see RestTemplate
 */
@Service
@RequiredArgsConstructor
public class LocationImplementation implements LocationService {
    private final RestTemplate rest;

    @Value("${application.map.location.iq.base.url.autocomplete}")
    private String LOCATION_IQ_AUTOCOMPLETE_BASE_URL;

    @Value("${application.map.location.iq.access-token}")
    private String LOCATION_IQ_ACCESS_TOKEN;

    @Override
    public ApiResponse<List<Address>> predictions(String query) {
        String url = String.format("%s?key=%s&q=%s&format=json", LOCATION_IQ_AUTOCOMPLETE_BASE_URL, LOCATION_IQ_ACCESS_TOKEN, query);

        ResponseEntity<List<LocationIQPlace>> response = rest.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

        List<Address> predictions = new ArrayList<>();
        if(response.getStatusCode().is2xxSuccessful()) {
            if(ObjectUtils.isNotEmpty(Objects.requireNonNull(response.getBody()))) {
                response.getBody().forEach(place -> predictions.add(CoreMapper.INSTANCE.address(place)));
                return new ApiResponse<>(predictions);
            }
        }
        return new ApiResponse<>(predictions);
    }

    @Override
    public ApiResponse<Address> search(String id) {
        return null;
    }
}
