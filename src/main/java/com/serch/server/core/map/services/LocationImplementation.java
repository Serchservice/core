package com.serch.server.core.map.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.core.map.requests.LocationRequest;
import com.serch.server.core.map.requests.NearbySearchRequest;
import com.serch.server.core.map.responses.Address;
import com.serch.server.core.map.responses.MapAddress;
import com.serch.server.core.map.responses.MapSuggestionsResponse;
import com.serch.server.core.map.responses.PlacesResponse;
import com.serch.server.enums.ServerHeader;
import com.serch.server.enums.shop.ShopStatus;
import com.serch.server.nearby.services.drive.services.NearbyDriveService;
import com.serch.server.domains.shop.responses.SearchShopResponse;
import com.serch.server.domains.shop.responses.ShopResponse;
import com.serch.server.utils.HelperUtil;
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
     * Constructs and returns HTTP headers for making API requests.
     *
     * @return HttpHeaders containing the necessary headers for API requests.
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
                Address address = mapToAddress(response.getBody());
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
                        response.getBody().getPlaces().forEach(place ->
                                shops.add(shop(place, latitude, longitude, getCategory(keyword, category)))
                        );
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
        log.info(String.format("Nearby Search for - %s", request));

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

    private SearchShopResponse shop(PlacesResponse.Place place, double latitude, double longitude, String type) {
        SearchShopResponse response = new SearchShopResponse();

        if (place.getLocation() != null) {
            double distance = HelperUtil.getDistance(latitude, longitude, place.getLocation().getLatitude(), place.getLocation().getLongitude());

            response.setDistanceInKm(distance + " km");
            response.setDistance(distance);
        }

        response.setShop(shop(place, type));
        response.setGoogle(true);

        return response;
    }

    private ShopResponse shop(PlacesResponse.Place place, String type) {
        if ( place == null ) {
            return null;
        }

        ShopResponse shop = new ShopResponse();
        shop.setCategory(type.replaceAll("_", " "));

        if(place.getInternationalPhoneNumber() != null) {
            shop.setPhone( place.getInternationalPhoneNumber() );
        } else if(place.getNationalPhoneNumber() != null) {
            shop.setPhone( place.getNationalPhoneNumber() );
        }

        if (place.getDisplayName() != null) {
            shop.setName(place.getDisplayName().getText());

            if(place.getShortFormattedAddress() != null) {
                shop.setAddress(String.format("%s, %s", place.getDisplayName().getText(), place.getShortFormattedAddress()));
            }
        } else if(place.getFormattedAddress() != null) {
            shop.setAddress(place.getFormattedAddress());
        }

        if(place.getGoogleMapsLinks() != null) {
            shop.setLink(place.getGoogleMapsLinks().getPlaceUri());
        }

        shop.setImage(place.getIconMaskBaseUri());
        shop.setStatus( place.getBusinessStatus().equalsIgnoreCase("operational") ? ShopStatus.OPEN : ShopStatus.SUSPENDED );

        if(place.getRating() != null) {
            shop.setRating( place.getRating() );
        }

        if(place.getCurrentOpeningHours() != null) {
            shop.setOpen(place.getCurrentOpeningHours().getOpenNow());
        }

        shop.setId( place.getId() );

        if (place.getLocation() != null) {
            shop.setLatitude( place.getLocation().getLatitude() );
            shop.setLongitude( place.getLocation().getLongitude() );
        }

        return shop;
    }
}
