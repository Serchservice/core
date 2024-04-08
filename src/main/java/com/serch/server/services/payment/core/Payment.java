package com.serch.server.services.payment.core;

import com.serch.server.exceptions.PaymentException;
import com.serch.server.services.payment.requests.InitializePaymentRequest;
import com.serch.server.services.payment.responses.InitializePaymentResponse;
import com.serch.server.services.payment.responses.InitializePaymentData;
import com.serch.server.services.payment.responses.PaymentVerificationResponse;
import com.serch.server.services.payment.responses.PaymentVerificationResponseData;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class Payment implements PaymentService {
    private final RestTemplate rest;

    @Value("${serch.paystack.test-secret-key}")
    private String TEST_SECRET_KEY;
    private final String BASE_API_ENDPOINT = "https://api.paystack.co";

    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer "+ TEST_SECRET_KEY);
        return headers;
    }

    @Override
    public InitializePaymentData initialize(InitializePaymentRequest request) {
        InitializePaymentRequest payment = request.validate();
        HttpEntity<InitializePaymentRequest> entity = new HttpEntity<>(payment, headers());
        String endpoint = BASE_API_ENDPOINT + "/initialize";
        ResponseEntity<InitializePaymentResponse> response = rest.postForEntity(endpoint, entity, InitializePaymentResponse.class);

        System.out.println(response.getBody());
        if(response.getStatusCode().is2xxSuccessful()) {
            InitializePaymentResponse body = response.getBody();
            if(Objects.requireNonNull(body).getStatus() && ObjectUtils.isEmpty(body.getData())) {
                return body.getData();
            } else {
                throw new PaymentException(Objects.requireNonNull(body).getMessage());
            }
        }
        throw new PaymentException("Error processing verification. Check your internet and try again.");
    }

    @Override
    public PaymentVerificationResponseData verify(String reference) {
        HttpEntity<Object> entity = new HttpEntity<>(headers());
        String endpoint = BASE_API_ENDPOINT + "/verify/" + reference;
        ResponseEntity<PaymentVerificationResponse> response = rest.exchange(endpoint, HttpMethod.GET, entity, PaymentVerificationResponse.class);

        System.out.println(response.getBody());
        if(response.getStatusCode().is2xxSuccessful()) {
            PaymentVerificationResponse body = response.getBody();
            if(Objects.requireNonNull(body).getStatus() && ObjectUtils.isEmpty(body.getData()) && body.getData().getStatus().equalsIgnoreCase("success")) {
                return body.getData();
            } else {
                throw new PaymentException(Objects.requireNonNull(body).getMessage());
            }
        }
        throw new PaymentException("Error processing verification. Check your internet and try again.");
    }
}
