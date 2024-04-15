package com.serch.server.services.payment.core;

import com.serch.server.exceptions.others.PaymentException;
import com.serch.server.services.payment.requests.InitializePaymentRequest;
import com.serch.server.services.payment.requests.PaymentChargeRequest;
import com.serch.server.services.payment.responses.InitializePaymentResponse;
import com.serch.server.services.payment.responses.InitializePaymentData;
import com.serch.server.services.payment.responses.PaymentVerificationResponse;
import com.serch.server.services.payment.responses.PaymentVerificationData;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

/**
 * Service class implementing PaymentService interface, providing functionality
 * for initializing payments, verifying payments, and charging authorization for payments.
 * This class interacts with the Paystack API to perform payment-related operations.
 * <p></p>
 * This wraps it interface class {@link PaymentService}
 * @see RestTemplate
 */
@Service
@RequiredArgsConstructor
public class Payment implements PaymentService {
    private final RestTemplate rest;

    @Value("${application.payment.api-key}")
    private String TEST_SECRET_KEY;
    private final String BASE_API_ENDPOINT = "https://api.paystack.co";

    /**
     * Constructs and returns HTTP headers for making API requests.
     *
     * @return HttpHeaders containing necessary headers for API requests.
     */
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

        System.out.println("Initialize Method in Payment - " + response.getBody());
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
    public PaymentVerificationData verify(String reference) {
        HttpEntity<Object> entity = new HttpEntity<>(headers());
        String endpoint = BASE_API_ENDPOINT + "/verify/" + reference;
        ResponseEntity<PaymentVerificationResponse> response = rest.exchange(endpoint, HttpMethod.GET, entity, PaymentVerificationResponse.class);

        System.out.println("Verify Method in Payment - " + response.getBody());
        return getPaymentVerificationData(response);
    }

    @Override
    public PaymentVerificationData charge(PaymentChargeRequest request) {
        PaymentChargeRequest charge = request.validate();
        HttpEntity<Object> entity = new HttpEntity<>(charge, headers());
        String endpoint = BASE_API_ENDPOINT + "/transaction/charge_authorization" ;
        ResponseEntity<PaymentVerificationResponse> response = rest.postForEntity(endpoint, entity, PaymentVerificationResponse.class);

        System.out.println("Charge Method in Payment - " + response.getBody());
        return getPaymentVerificationData(response);
    }

    private static PaymentVerificationData getPaymentVerificationData(ResponseEntity<PaymentVerificationResponse> response) {
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
