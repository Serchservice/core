package com.serch.server.services.map.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.exceptions.others.MapException;
import com.serch.server.services.map.responses.Place;
import com.serch.server.services.map.responses.PlaceResponse;
import com.serch.server.services.map.responses.Prediction;
import com.serch.server.services.map.responses.PredictionListResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

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

    @Value("${application.map.api-key}")
    private String API_KEY;

    private String MAP_BASE_URL(String destination) {
        return "https://maps.googleapis.com/maps/api/place%s".formatted(destination);
    }

    @Override
    public ApiResponse<List<Prediction>> predictions(String address) {
        var response = rest.getForEntity(
                MAP_BASE_URL("/autocomplete/json?input=%s&key=%s&sessiontoken=%s"
                        .formatted(address, API_KEY, UUID.randomUUID())
                ),
                PredictionListResponse.class
        );
        System.out.println(response);
        List<Prediction> predictions = new ArrayList<>();
        if(response.getStatusCode().is2xxSuccessful()) {
            if(ObjectUtils.isNotEmpty(Objects.requireNonNull(response.getBody()).getPredictions())) {
                for (var prediction : response.getBody().getPredictions()) {
                    extracted(prediction, predictions);
                }
                return new ApiResponse<>(predictions);
            } else {
                throw new MapException("Couldn't find location");
            }
        } else {
            throw new MapException("An error occurred while looking for location");
        }
    }

    private static void extracted(PredictionListResponse.PlacePrediction prediction, List<Prediction> predictions) {
        Prediction predict = new Prediction();
        predict.setDescription(prediction.getDescription());
        predict.setReference(prediction.getReference());
        predict.setMainText(prediction.getAddress().getMainText());
        predict.setSecondaryText(prediction.getAddress().getSecondaryText());
        predict.setPlaceId(prediction.getPlaceId());
        predictions.add(predict);
    }

    @Override
    public ApiResponse<Place> search(String id) {
        var response = rest.getForEntity(
                MAP_BASE_URL("/details/json?place_id=%s&key=%s".formatted(id, API_KEY)),
                PlaceResponse.class
        );
        System.out.println(response);
        var data = response.getBody();
        if(response.getStatusCode().is2xxSuccessful()) {
            if(Objects.requireNonNull(data).getResult().getGeometry() != null
                && ObjectUtils.isNotEmpty(Objects.requireNonNull(data).getResult().getAddressList())
            ) {
                Place place = getPlace(data);
                return new ApiResponse<>(place);
            } else {
                throw new MapException("Couldn't find the place you are looking for");
            }
        } else {
            throw new MapException("An error occurred while looking for location");
        }
    }

    private static Place getPlace(PlaceResponse data) {
        Place place = new Place();
        place.setAddress(data.getResult().getAddress());
        place.setPlaceId(data.getResult().getPlaceId());
        place.setLatitude(data.getResult().getGeometry().getLocation().getLat());
        place.setLongitude(data.getResult().getGeometry().getLocation().getLng());
        place.setShortName(data.getResult().getAddressList().getFirst().getShortName());
        place.setLongName(data.getResult().getAddressList().getFirst().getLongName());
        return place;
    }
}
