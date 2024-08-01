package com.serch.server.core.payment.core;

import com.serch.server.core.payment.requests.InitializePaymentRequest;
import com.serch.server.core.payment.requests.PaymentChargeRequest;
import com.serch.server.core.payment.responses.*;
import com.serch.server.exceptions.others.PaymentException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

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
    @Value("${application.payment.base-url}")
    private String BASE_API_ENDPOINT;

    /**
     * Constructs and returns HTTP headers for making API requests.
     *
     * @return HttpHeaders containing the necessary headers for API requests.
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
        String endpoint = BASE_API_ENDPOINT + "/transaction/initialize";
        ResponseEntity<InitializePaymentResponse> response = rest.postForEntity(endpoint, entity, InitializePaymentResponse.class);

        System.out.println("Initialize Method in Payment - " + response.getBody());
        if(response.getStatusCode().is2xxSuccessful()) {
            InitializePaymentResponse body = response.getBody();
            assert body != null : "Couldn't initialize payment";
            return body.getData();
        }
        throw new PaymentException("Error processing verification. Check your internet and try again.");
    }

    @Override
    public PaymentVerificationData verify(String reference) {
        HttpEntity<Object> entity = new HttpEntity<>(headers());
        String endpoint = BASE_API_ENDPOINT + "/transaction/verify/" + reference;
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
            assert body != null : "Couldn't verify payment";
            if(body.getData().getStatus().equalsIgnoreCase("success")) {
                return body.getData();
            } else {
                throw new PaymentException("Couldn't verify your transaction. Try again");
            }
        }
        throw new PaymentException("Error processing verification. Check your internet and try again.");
    }

    @Override
    public List<Bank> banks() {
        HttpEntity<Object> entity = new HttpEntity<>(headers());
        String endpoint = BASE_API_ENDPOINT + "/bank" ;
        ResponseEntity<Banks> response = rest.exchange(endpoint, HttpMethod.GET, entity, Banks.class);

        if(response.getStatusCode().is2xxSuccessful() && response.getBody() != null && response.getBody().getData() != null) {
            return response.getBody().getData();
        }
        return List.of();
    }

    @Override
    public BankAccount verify(String accountNumber, String code) {
        HttpEntity<Object> entity = new HttpEntity<>(headers());
        String endpoint = BASE_API_ENDPOINT + "/bank/resolve?account_number=%s&bank_code=%s".formatted(accountNumber, code) ;
        try {
            ResponseEntity<BankAccountResponse> response = rest.exchange(endpoint, HttpMethod.GET, entity, BankAccountResponse.class);

            if(response.getStatusCode().is2xxSuccessful() && response.getBody() != null && response.getBody().getData() != null) {
                return response.getBody().getData();
            } else {
                throw new PaymentException("Bank account not found. Verify your details");
            }
        } catch (Exception ignored) {
            throw new PaymentException("Bank account not found. Verify your details");
        }
    }
}