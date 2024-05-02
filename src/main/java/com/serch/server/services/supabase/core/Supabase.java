package com.serch.server.services.supabase.core;

import com.serch.server.services.supabase.responses.Country;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class Supabase implements SupabaseService {
    @Value("${application.supabase.api-key}")
    private String API_KEY;
    @Value("${application.supabase.base-url}")
    private String BASE_URL;

    private final RestTemplate rest;

    /**
     * Constructs and returns HTTP headers for making API requests.
     *
     * @return HttpHeaders containing the necessary headers for API requests.
     */
    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("apiKey", API_KEY);
        headers.add("Authorization", "Bearer "+ API_KEY);
        return headers;
    }

    @Override
    public List<Country> getCountries() {
        HttpEntity<Object> entity = new HttpEntity<>(headers());
        String endpoint = BASE_URL + "/countries";
        ResponseEntity<List<Country>> response = rest.exchange(endpoint, HttpMethod.GET, entity, ParameterizedTypeReference.forType(List.class));
        if(response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            return List.of();
        }
    }
}
