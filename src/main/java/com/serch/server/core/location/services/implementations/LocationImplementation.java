package com.serch.server.core.location.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.core.location.requests.LocationRequest;
import com.serch.server.core.location.requests.NearbySearchRequest;
import com.serch.server.core.location.responses.Address;
import com.serch.server.core.location.responses.MapAddress;
import com.serch.server.core.location.responses.MapSuggestionsResponse;
import com.serch.server.core.location.responses.PlacesResponse;
import com.serch.server.core.location.services.LocationService;
import com.serch.server.domains.shop.responses.SearchShopResponse;
import com.serch.server.enums.ServerHeader;
import com.serch.server.mappers.CoreMapper;
import com.serch.server.domains.nearby.services.drive.services.NearbyDriveService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
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
    private static final Logger log = LoggerFactory.getLogger(LocationImplementation.class);

    private final NearbyDriveService driveService;
    private final RestTemplate rest;

    @Value("${application.map.api-key}")
    private String MAP_API_KEY;

    /**
     * Constructs and returns HTTP headers for making API dtos.
     *
     * @return HttpHeaders containing the necessary headers for API dtos.
     */
    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(ServerHeader.GOOGLE_API_KEY.getValue(), MAP_API_KEY);

        return headers;
    }

    @Override
    public ApiResponse<List<Address>> predictions(String query) {
        ResponseEntity<MapSuggestionsResponse> response = getPredictionResult(query);

        List<Address> predictions = new ArrayList<>();
        if(response.getStatusCode().is2xxSuccessful()) {
            if(ObjectUtils.isNotEmpty(Objects.requireNonNull(response.getBody()))) {
                if(response.getBody().getSuggestions() != null && !response.getBody().getSuggestions().isEmpty()) {
                    response.getBody().getSuggestions()
                            .forEach(place -> predictions.add(getPrediction(place.getPlacePrediction())));
                }
            }
        }

        return new ApiResponse<>(predictions);
    }

    private ResponseEntity<MapSuggestionsResponse> getPredictionResult(String query) {
        LocationRequest request = new LocationRequest();
        request.setInput(query);

        HttpHeaders headers = headers();
        headers.set(ServerHeader.GOOGLE_FIELD_MASK.getValue(), "suggestions.placePrediction.placeId");
        HttpEntity<Object> entity = new HttpEntity<>(request, headers);

        String MAP_AUTOCOMPLETE_BASE_URL = "https://places.googleapis.com/v1/places:autocomplete";

        return rest.exchange(MAP_AUTOCOMPLETE_BASE_URL, HttpMethod.POST, entity, MapSuggestionsResponse.class);
    }

    private Address getPrediction( MapSuggestionsResponse.PlacePrediction prediction) {
        ResponseEntity<MapAddress> response = getAddressResult(prediction);

        if(response.getStatusCode().is2xxSuccessful()) {
            if(ObjectUtils.isNotEmpty(Objects.requireNonNull(response.getBody()))) {
                Address address = CoreMapper.instance.address(response.getBody());
                address.setId(prediction.getPlaceId());
                return address;
            }
        }

        return new Address();
    }

    private ResponseEntity<MapAddress> getAddressResult(MapSuggestionsResponse.PlacePrediction prediction) {
        String fields = "addressComponents,location,displayName,formattedAddress,shortFormattedAddress";
        String url = String.format(
                "https://places.googleapis.com/v1/places/%s?fields=%s&key=%s",
                prediction.getPlaceId(), fields, MAP_API_KEY
        );
        HttpEntity<Object> entity = new HttpEntity<>(headers());

        return rest.exchange(url, HttpMethod.GET, entity, MapAddress.class);
    }

    @Override
    public List<SearchShopResponse> nearbySearch(String keyword, String category, Double longitude, Double latitude, Double radius) {
        HttpEntity<Object> entity = getNearbySearchResult(keyword, category, longitude, latitude, radius);

        List<SearchShopResponse> shops = new ArrayList<>();

        try {
            String MAP_NEARBY_BASE_URL = "https://places.googleapis.com/v1/places:searchNearby";
            ResponseEntity<PlacesResponse> response = rest.exchange(
                    MAP_NEARBY_BASE_URL,
                    HttpMethod.POST,
                    entity,
                    PlacesResponse.class
            );

            if(response.getStatusCode().is2xxSuccessful()) {
                if(ObjectUtils.isNotEmpty(Objects.requireNonNull(response.getBody()))) {
                    if(response.getBody().getPlaces() != null && !response.getBody().getPlaces().isEmpty()) {
                        response.getBody().getPlaces().forEach(place -> shops.add(CoreMapper.instance.response(
                                place,
                                latitude,
                                longitude,
                                getCategory(keyword, category)
                        )));
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return shops;
    }

    private HttpEntity<Object> getNearbySearchResult(String keyword, String category, Double longitude, Double latitude, Double radius) {
        NearbySearchRequest request = new NearbySearchRequest();
        request.setIncludedTypes(new ArrayList<>(Collections.singletonList((getCategory(keyword, category)))));

        request.getLocationRestriction().getCircle().setRadius(radius);
        request.getLocationRestriction().getCircle().getCenter().setLatitude(latitude);
        request.getLocationRestriction().getCircle().getCenter().setLongitude(longitude);
        log.info("Nearby Search for - {}", request);

        driveService.search(getCategory(keyword, category));

        HttpHeaders headers = headers();
        headers.set(ServerHeader.GOOGLE_FIELD_MASK.getValue(), getFieldMasks());

        return new HttpEntity<>(request, headers);
    }

    private String getCategory(String keyword, String category) {
        return (category == null || category.isEmpty() ? keyword : category.equalsIgnoreCase("mechanic") ? "car_repair" : category).toLowerCase();
    }

    private String getFieldMasks() {
        return "places.displayName,places.formattedAddress,places.shortFormattedAddress,places.location,places.id," +
                "places.nationalPhoneNumber,places.internationalPhoneNumber,places.rating,places.googleMapsUri," +
                "places.websiteUri,places.businessStatus,places.googleMapsLinks,places.iconMaskBaseUri," +
                "places.currentOpeningHours.openNow";
    }
}