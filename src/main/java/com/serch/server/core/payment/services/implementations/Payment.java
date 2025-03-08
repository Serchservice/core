package com.serch.server.core.payment.services.implementations;

import com.serch.server.core.payment.requests.InitializePaymentRequest;
import com.serch.server.core.payment.requests.PaymentChargeRequest;
import com.serch.server.core.payment.responses.*;
import com.serch.server.core.payment.services.PaymentService;
import com.serch.server.exceptions.others.PaymentException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;

/**
 * Service class implementing PaymentService interface, providing functionality
 * for initializing payments, verifying payments, and charging auth for payments.
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
    private String SECRET_KEY;

    @Value("${application.payment.base-url}")
    private String BASE_API_ENDPOINT;

    /**
     * Constructs and returns HTTP headers for making API dtos.
     *
     * @return HttpHeaders containing the necessary headers for API dtos.
     */
    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer "+ SECRET_KEY);

        return headers;
    }

    private String buildEndpoint(String endpoint) {
        return BASE_API_ENDPOINT + endpoint;
    }

    private boolean qualify(ResponseEntity<?> response) {
        return response.getStatusCode().is2xxSuccessful() && response.getBody() != null;
    }

    @Override
    public InitializePaymentData initialize(InitializePaymentRequest request) {
        InitializePaymentRequest payment = request.validate();

        ResponseEntity<InitializePaymentResponse> response = rest.postForEntity(
                buildEndpoint("/transaction/initialize"),
                new HttpEntity<>(payment, headers()),
                InitializePaymentResponse.class
        );

        if(response.getStatusCode().is2xxSuccessful()) {
            InitializePaymentResponse body = response.getBody();
            assert body != null : "Couldn't initialize payment";
            return body.getData();
        }

        throw new PaymentException("Error processing verification. Check your internet and try again.");
    }

    @Override
    public PaymentVerificationData verify(String reference) {
        ResponseEntity<PaymentVerificationResponse> response = rest.exchange(
                buildEndpoint("/transaction/verify/" + reference),
                HttpMethod.GET,
                new HttpEntity<>(headers()),
                PaymentVerificationResponse.class
        );

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
    public PaymentVerificationData charge(PaymentChargeRequest request) {
        PaymentChargeRequest charge = request.validate();

        ResponseEntity<PaymentVerificationResponse> response = rest.postForEntity(
                buildEndpoint("/transaction/charge_authorization"),
                new HttpEntity<>(charge, headers()),
                PaymentVerificationResponse.class
        );

        if(response.getStatusCode().is2xxSuccessful()) {
            PaymentVerificationResponse body = response.getBody();
            assert body != null : "Couldn't complete payment";

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
        ResponseEntity<Banks> response = rest.exchange(buildEndpoint("/bank"), HttpMethod.GET, new HttpEntity<>(headers()), Banks.class);

        if(qualify(response) && Objects.requireNonNull(response.getBody()).getData() != null) {
            return response.getBody().getData();
        } else {
            return List.of();
        }
    }

    @Override
    public BankAccount verify(String accountNumber, String code) {
        try {
            ResponseEntity<BankAccountResponse> response = rest.exchange(
                    buildEndpoint("/bank/resolve?account_number=%s&bank_code=%s".formatted(accountNumber, code)),
                    HttpMethod.GET,
                    new HttpEntity<>(headers()),
                    BankAccountResponse.class
            );

            if(qualify(response) && Objects.requireNonNull(response.getBody()).getData() != null) {
                return response.getBody().getData();
            } else {
                throw new PaymentException("Bank account not found. Verify your details");
            }
        } catch (Exception ignored) {
            throw new PaymentException("Bank account not found. Verify your details");
        }
    }
}