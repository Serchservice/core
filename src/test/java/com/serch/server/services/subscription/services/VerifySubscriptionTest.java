package com.serch.server.services.subscription.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.serch.server.models.subscription.SubscriptionAuth;
import com.serch.server.repositories.auth.incomplete.IncompleteRepository;
import com.serch.server.repositories.subscription.SubscriptionAuthRepository;
import com.serch.server.repositories.subscription.SubscriptionRepository;
import com.serch.server.repositories.subscription.SubscriptionRequestRepository;
import com.serch.server.services.account.services.AdditionalService;
import com.serch.server.services.account.services.ProfileService;
import com.serch.server.services.account.services.SpecialtyService;
import com.serch.server.services.auth.services.AuthService;
import com.serch.server.services.auth.services.ProviderAuthService;
import com.serch.server.services.payment.core.PaymentService;
import com.serch.server.services.payment.responses.PaymentAuthorization;
import com.serch.server.services.payment.responses.PaymentVerificationData;
import com.serch.server.services.transaction.services.InvoiceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {VerifySubscription.class})
@ExtendWith(SpringExtension.class)
class VerifySubscriptionTest {
    @MockBean
    private AdditionalService additionalService;

    @MockBean
    private AuthService authService;

    @MockBean
    private IncompleteRepository incompleteRepository;

    @MockBean
    private InvoiceService invoiceService;

    @MockBean
    private PaymentService paymentService;

    @MockBean
    private ProfileService profileService;

    @MockBean
    private ProviderAuthService providerAuthService;

    @MockBean
    private SpecialtyService specialtyService;

    @MockBean
    private SubscriptionAuthRepository subscriptionAuthRepository;

    @MockBean
    private SubscriptionRepository subscriptionRepository;

    @MockBean
    private SubscriptionRequestRepository subscriptionRequestRepository;

    @Autowired
    private VerifySubscription verifySubscription;

    /**
     * Method under test:
     * {@link VerifySubscription#createAuth(String, PaymentVerificationData)}
     */
    @Test
    void testCreateAuth() {
        // Arrange
        PaymentAuthorization authorization = new PaymentAuthorization();
        authorization.setAccountName("Dr Jane Doe");
        authorization.setAuthorizationCode("JaneDoe");
        authorization.setBank("Bank");
        authorization.setBin("Bin");
        authorization.setCardType("Card Type");
        authorization.setChannel("Channel");
        authorization.setCountryCode("GB");
        authorization.setExpMonth("Exp Month");
        authorization.setExpYear("Exp Year");
        authorization.setLast4("Last4");
        authorization.setReusable(true);
        authorization.setSignature("Signature");

        PaymentVerificationData response = new PaymentVerificationData();
        response.setAmount(10);
        response.setAuthorization(authorization);
        response.setChannel("Channel");
        response.setCurrency("GBP");
        response.setFees(1);
        response.setId(1L);
        response.setIpAddress("42 Main St");
        response.setMessage("Not all who wander are lost");
        response.setReference("Reference");
        response.setStatus("Status");

        // Act
        SubscriptionAuth actualCreateAuthResult = verifySubscription.createAuth("42 Main St", response);

        // Assert
        assertEquals("42 Main St", actualCreateAuthResult.getEmailAddress());
        assertEquals("Bank", actualCreateAuthResult.getBank());
        assertEquals("Bin", actualCreateAuthResult.getBin());
        assertEquals("Card Type", actualCreateAuthResult.getCardType());
        assertEquals("Channel", actualCreateAuthResult.getChannel());
        assertEquals("Dr Jane Doe", actualCreateAuthResult.getAccountName());
        assertEquals("Exp Month", actualCreateAuthResult.getExpMonth());
        assertEquals("Exp Year", actualCreateAuthResult.getExpYear());
        assertEquals("GB", actualCreateAuthResult.getCountryCode());
        assertEquals("JaneDoe", actualCreateAuthResult.getCode());
        assertEquals("Last4", actualCreateAuthResult.getLast4());
        assertEquals("Signature", actualCreateAuthResult.getSignature());
        assertTrue(actualCreateAuthResult.getReusable());
    }

    /**
     * Method under test:
     * {@link VerifySubscription#createAuth(String, PaymentVerificationData)}
     */
    @Test
    void testCreateAuth2() {
        // Arrange
        PaymentAuthorization authorization = new PaymentAuthorization();
        authorization.setAccountName("Dr Jane Doe");
        authorization.setAuthorizationCode("JaneDoe");
        authorization.setBank("Bank");
        authorization.setBin("Bin");
        authorization.setCardType("Card Type");
        authorization.setChannel("Channel");
        authorization.setCountryCode("GB");
        authorization.setExpMonth("Exp Month");
        authorization.setExpYear("Exp Year");
        authorization.setLast4("Last4");
        authorization.setReusable(true);
        authorization.setSignature("Signature");

        PaymentVerificationData response = new PaymentVerificationData();
        response.setAmount(10);
        response.setAuthorization(authorization);
        response.setChannel("Channel");
        response.setCurrency("GBP");
        response.setFees(1);
        response.setId(1L);
        response.setIpAddress("42 Main St");
        response.setMessage("Not all who wander are lost");
        response.setReference("Reference");
        response.setStatus("Status");

        // Act
        SubscriptionAuth actualCreateAuthResult = verifySubscription.createAuth("17 High St", response);

        // Assert
        assertEquals("17 High St", actualCreateAuthResult.getEmailAddress());
        assertEquals("Bank", actualCreateAuthResult.getBank());
        assertEquals("Bin", actualCreateAuthResult.getBin());
        assertEquals("Card Type", actualCreateAuthResult.getCardType());
        assertEquals("Channel", actualCreateAuthResult.getChannel());
        assertEquals("Dr Jane Doe", actualCreateAuthResult.getAccountName());
        assertEquals("Exp Month", actualCreateAuthResult.getExpMonth());
        assertEquals("Exp Year", actualCreateAuthResult.getExpYear());
        assertEquals("GB", actualCreateAuthResult.getCountryCode());
        assertEquals("JaneDoe", actualCreateAuthResult.getCode());
        assertEquals("Last4", actualCreateAuthResult.getLast4());
        assertEquals("Signature", actualCreateAuthResult.getSignature());
        assertTrue(actualCreateAuthResult.getReusable());
    }

    /**
     * Method under test:
     * {@link VerifySubscription#createAuth(String, PaymentVerificationData)}
     */
    @Test
    void testCreateAuth3() {
        // Arrange
        PaymentAuthorization authorization = new PaymentAuthorization();
        authorization.setAccountName("Dr Jane Doe");
        authorization.setAuthorizationCode("JaneDoe");
        authorization.setBank("Bank");
        authorization.setBin("Bin");
        authorization.setCardType("Card Type");
        authorization.setChannel("Channel");
        authorization.setCountryCode("GB");
        authorization.setExpMonth("Exp Month");
        authorization.setExpYear("Exp Year");
        authorization.setLast4("Last4");
        authorization.setReusable(true);
        authorization.setSignature("Signature");

        PaymentAuthorization paymentAuthorization = new PaymentAuthorization();
        paymentAuthorization.setAccountName("Dr Jane Doe");
        paymentAuthorization.setAuthorizationCode("JaneDoe");
        paymentAuthorization.setBank("Bank");
        paymentAuthorization.setBin("Bin");
        paymentAuthorization.setCardType("Card Type");
        paymentAuthorization.setChannel("Channel");
        paymentAuthorization.setCountryCode("GB");
        paymentAuthorization.setExpMonth("Exp Month");
        paymentAuthorization.setExpYear("Exp Year");
        paymentAuthorization.setLast4("Last4");
        paymentAuthorization.setReusable(true);
        paymentAuthorization.setSignature("Signature");
        PaymentVerificationData response = mock(PaymentVerificationData.class);
        when(response.getAuthorization()).thenReturn(paymentAuthorization);
        doNothing().when(response).setAmount(Mockito.<Integer>any());
        doNothing().when(response).setAuthorization(Mockito.<PaymentAuthorization>any());
        doNothing().when(response).setChannel(Mockito.<String>any());
        doNothing().when(response).setCurrency(Mockito.<String>any());
        doNothing().when(response).setFees(Mockito.<Integer>any());
        doNothing().when(response).setId(Mockito.<Long>any());
        doNothing().when(response).setIpAddress(Mockito.<String>any());
        doNothing().when(response).setMessage(Mockito.<String>any());
        doNothing().when(response).setReference(Mockito.<String>any());
        doNothing().when(response).setStatus(Mockito.<String>any());
        response.setAmount(10);
        response.setAuthorization(authorization);
        response.setChannel("Channel");
        response.setCurrency("GBP");
        response.setFees(1);
        response.setId(1L);
        response.setIpAddress("42 Main St");
        response.setMessage("Not all who wander are lost");
        response.setReference("Reference");
        response.setStatus("Status");

        // Act
        SubscriptionAuth actualCreateAuthResult = verifySubscription.createAuth("42 Main St", response);

        // Assert
        verify(response).getAuthorization();
        verify(response).setAmount(Mockito.<Integer>any());
        verify(response).setAuthorization(Mockito.<PaymentAuthorization>any());
        verify(response).setChannel(Mockito.<String>any());
        verify(response).setCurrency(Mockito.<String>any());
        verify(response).setFees(Mockito.<Integer>any());
        verify(response).setId(Mockito.<Long>any());
        verify(response).setIpAddress(Mockito.<String>any());
        verify(response).setMessage(Mockito.<String>any());
        verify(response).setReference(Mockito.<String>any());
        verify(response).setStatus(Mockito.<String>any());
        assertEquals("42 Main St", actualCreateAuthResult.getEmailAddress());
        assertEquals("Bank", actualCreateAuthResult.getBank());
        assertEquals("Bin", actualCreateAuthResult.getBin());
        assertEquals("Card Type", actualCreateAuthResult.getCardType());
        assertEquals("Channel", actualCreateAuthResult.getChannel());
        assertEquals("Dr Jane Doe", actualCreateAuthResult.getAccountName());
        assertEquals("Exp Month", actualCreateAuthResult.getExpMonth());
        assertEquals("Exp Year", actualCreateAuthResult.getExpYear());
        assertEquals("GB", actualCreateAuthResult.getCountryCode());
        assertEquals("JaneDoe", actualCreateAuthResult.getCode());
        assertEquals("Last4", actualCreateAuthResult.getLast4());
        assertEquals("Signature", actualCreateAuthResult.getSignature());
        assertTrue(actualCreateAuthResult.getReusable());
    }
}
