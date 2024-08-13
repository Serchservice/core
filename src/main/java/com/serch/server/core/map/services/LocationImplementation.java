package com.serch.server.core.map.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.core.map.LocationRequest;
import com.serch.server.core.map.responses.Address;
import com.serch.server.core.map.responses.MapAddress;
import com.serch.server.core.map.responses.MapSuggestionsResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
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

    @Value("${application.map.base.url.autocomplete}")
    private String MAP_AUTOCOMPLETE_BASE_URL;

    @Value("${application.map.api-key}")
    private String MAP_API_KEY;

    /**
     * Constructs and returns HTTP headers for making API requests.
     *
     * @return HttpHeaders containing the necessary headers for API requests.
     */
    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("X-Goog-Api-Key", MAP_API_KEY);
        return headers;
    }

    @Override
    public ApiResponse<List<Address>> predictions(String query) {
        LocationRequest request = new LocationRequest();
        request.setInput(query);
        HttpEntity<Object> entity = new HttpEntity<>(request, headers());

        ResponseEntity<MapSuggestionsResponse> response = rest.exchange(
                MAP_AUTOCOMPLETE_BASE_URL,
                HttpMethod.POST,
                entity,
                MapSuggestionsResponse.class
        );

        List<Address> predictions = new ArrayList<>();
        if(response.getStatusCode().is2xxSuccessful()) {
            if(ObjectUtils.isNotEmpty(Objects.requireNonNull(response.getBody()))) {
                response.getBody().getSuggestions()
                        .forEach(place -> predictions.add(getPrediction(place.getPlacePrediction())));
            }
        }
        return new ApiResponse<>(predictions);
    }

    private Address getPrediction( MapSuggestionsResponse.PlacePrediction prediction) {
        String fields = "addressComponents,location,displayName,formattedAddress,shortFormattedAddress";
        String url = String.format(
                "https://places.googleapis.com/v1/places/%s?fields=%s&key=%s",
                prediction.getPlaceId(), fields, MAP_API_KEY
        );
        HttpEntity<Object> entity = new HttpEntity<>(headers());

        ResponseEntity<MapAddress> response = rest.exchange(url, HttpMethod.GET, entity, MapAddress.class);
        if(response.getStatusCode().is2xxSuccessful()) {
            if(ObjectUtils.isNotEmpty(Objects.requireNonNull(response.getBody()))) {
                Address address = mapToAddress(response.getBody());
                address.setId(prediction.getPlaceId());
                return address;
            }
        }
        return new Address();
    }

    private Address mapToAddress(MapAddress mapAddress) {
        Address address = new Address();

        if (mapAddress.getDisplayName() != null) {
            address.setPlace(mapAddress.getDisplayName().getText());

            if(mapAddress.getShortFormattedAddress() != null) {
                address.setPlace(String.format("%s, %s", address.getPlace(), mapAddress.getShortFormattedAddress()));
            }
        } else if(mapAddress.getFormattedAddress() != null) {
            address.setPlace(mapAddress.getFormattedAddress());
        }

        // Set latitude and longitude
        if (mapAddress.getLocation() != null) {
            address.setLatitude(mapAddress.getLocation().getLatitude());
            address.setLongitude(mapAddress.getLocation().getLongitude());
        }

        // Iterate over addressComponents to map fields
        for (MapAddress.AddressComponent component : mapAddress.getAddressComponents()) {
            if (component.getTypes().contains("country")) {
                address.setCountry(component.getLongText());
            } else if (component.getTypes().contains("administrative_area_level_1")) {
                address.setState(component.getLongText());
            } else if (component.getTypes().contains("locality")) {
                address.setCity(component.getLongText());
            } else if (component.getTypes().contains("administrative_area_level_2")) {
                address.setLocalGovernmentArea(component.getLongText());
            } else if (component.getTypes().contains("plus_code")) {
                address.setStreetNumber(component.getLongText());
            } else if (component.getTypes().contains("administrative_area_level_3")) {
                address.setStreetName(component.getLongText());
            }
        }

        return address;
    }

    @Override
    public ApiResponse<Address> search(String id) {
        return null;
    }
}
