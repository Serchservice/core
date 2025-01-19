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
        ResponseEntity<InitializePaymentResponse> response = getInitializeResponse(request);

        if(response.getStatusCode().is2xxSuccessful()) {
            InitializePaymentResponse body = response.getBody();
            assert body != null : "Couldn't initialize payment";
            return body.getData();
        }

        throw new PaymentException("Error processing verification. Check your internet and try again.");
    }

    private ResponseEntity<InitializePaymentResponse> getInitializeResponse(InitializePaymentRequest request) {
        InitializePaymentRequest payment = request.validate();
        HttpEntity<InitializePaymentRequest> entity = new HttpEntity<>(payment, headers());
        String endpoint = BASE_API_ENDPOINT + "/transaction/initialize";

        return rest.postForEntity(endpoint, entity, InitializePaymentResponse.class);
    }

    @Override
    public PaymentVerificationData verify(String reference) {
        return getPaymentVerificationData(getVerifyResponse(reference));
    }

    private ResponseEntity<PaymentVerificationResponse> getVerifyResponse(String reference) {
        HttpEntity<Object> entity = new HttpEntity<>(headers());
        String endpoint = BASE_API_ENDPOINT + "/transaction/verify/" + reference;

        return rest.exchange(endpoint, HttpMethod.GET, entity, PaymentVerificationResponse.class);
    }

    @Override
    public PaymentVerificationData charge(PaymentChargeRequest request) {
        return getPaymentVerificationData(getChargeResponse(request));
    }

    private ResponseEntity<PaymentVerificationResponse> getChargeResponse(PaymentChargeRequest request) {
        PaymentChargeRequest charge = request.validate();
        HttpEntity<Object> entity = new HttpEntity<>(charge, headers());
        String endpoint = BASE_API_ENDPOINT + "/transaction/charge_authorization";

        return rest.postForEntity(endpoint, entity, PaymentVerificationResponse.class);
    }

    private PaymentVerificationData getPaymentVerificationData(ResponseEntity<PaymentVerificationResponse> response) {
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
        ResponseEntity<Banks> response = getListOfBankResponse();

        if(response.getStatusCode().is2xxSuccessful() && response.getBody() != null && response.getBody().getData() != null) {
            return response.getBody().getData();
        }
        return List.of();
    }

    private ResponseEntity<Banks> getListOfBankResponse() {
        HttpEntity<Object> entity = new HttpEntity<>(headers());
        String endpoint = BASE_API_ENDPOINT + "/bank" ;

        return rest.exchange(endpoint, HttpMethod.GET, entity, Banks.class);
    }

    @Override
    public BankAccount verify(String accountNumber, String code) {
        HttpEntity<Object> entity = new HttpEntity<>(headers());
        String endpoint = BASE_API_ENDPOINT + "/bank/resolve?account_number=%s&bank_code=%s".formatted(accountNumber, code);

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