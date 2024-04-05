package com.serch.server.services.email.services;

import com.resend.services.emails.model.SendEmailResponse;
import com.serch.server.bases.ApiResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {EmailAuthSender.class})
@ExtendWith(SpringExtension.class)
class EmailAuthSenderTest {
    @Autowired
    private EmailAuthSender emailAuthSender;

    @MockBean
    private EmailService emailService;

    /**
     * Method under test: {@link EmailAuthSender#sendSignup(String, String)}
     */
    @Test
    void testSendSignup() {
        // Arrange
        ApiResponse<SendEmailResponse> apiResponse = new ApiResponse<>(new SendEmailResponse("42"));
        when(emailService.send(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
                anyBoolean())).thenReturn(apiResponse);

        // Act
        ApiResponse<SendEmailResponse> actualSendSignupResult = emailAuthSender.sendSignup("evaristusadimonyemma@gmail.com",
                "Not all who wander are lost");

        // Assert
        verify(emailService).send(Mockito.any(), Mockito.any(), Mockito.any(),
                Mockito.any(), anyBoolean());
        assertSame(apiResponse, actualSendSignupResult);
    }

    /**
     * Method under test: {@link EmailAuthSender#sendSignup(String, String)}
     */
    @Test
    void testSendSignup2() {
        // Arrange
        ApiResponse<SendEmailResponse> apiResponse = new ApiResponse<>(new SendEmailResponse("42"));
        when(emailService.send(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
                anyBoolean())).thenReturn(apiResponse);

        // Act
        ApiResponse<SendEmailResponse> actualSendSignupResult = emailAuthSender.sendSignup("evaristusadimonyemma@gmail.com", "676767 - test");

        // Assert
        verify(emailService).send(Mockito.any(), Mockito.any(), Mockito.any(),
                Mockito.any(), anyBoolean());
        assertSame(apiResponse, actualSendSignupResult);
    }

    /**
     * Method under test: {@link EmailAuthSender#sendReset(String, String, String)}
     */
    @Test
    void testSendReset() {
        // Arrange
        ApiResponse<SendEmailResponse> apiResponse = new ApiResponse<>(new SendEmailResponse("42"));
        when(emailService.send(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
                anyBoolean())).thenReturn(apiResponse);

        // Act
        ApiResponse<SendEmailResponse> actualSendResetResult = emailAuthSender.sendReset("evaristusadimonyemma@gmail.com", "Jane",
                "Not all who wander are lost");

        // Assert
        verify(emailService).send(Mockito.any(), Mockito.any(), Mockito.any(),
                Mockito.any(), anyBoolean());
        assertSame(apiResponse, actualSendResetResult);
    }

    /**
     * Method under test: {@link EmailAuthSender#sendReset(String, String, String)}
     */
    @Test
    void testSendReset2() {
        // Arrange
        ApiResponse<SendEmailResponse> apiResponse = new ApiResponse<>(new SendEmailResponse("42"));
        when(emailService.send(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
                anyBoolean())).thenReturn(apiResponse);

        // Act
        ApiResponse<SendEmailResponse> actualSendResetResult = emailAuthSender.sendReset("evaristusadimonyemma@gmail.com", "Jane",
                "678787 - Test");

        // Assert
        verify(emailService).send(Mockito.any(), Mockito.any(), Mockito.any(),
                Mockito.any(), anyBoolean());
        assertSame(apiResponse, actualSendResetResult);
    }
}
