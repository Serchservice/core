package com.serch.server.services.email.services;

import com.resend.services.emails.model.SendEmailResponse;
import com.serch.server.bases.ApiResponse;
import com.serch.server.exceptions.EmailException;
import com.serch.server.services.email.EmailType;
import com.serch.server.services.email.models.SendEmail;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

//    /**
//     * Method under test: {@link EmailAuthSender#send(SendEmail)}
//     */
//    @Test
//    void testSend() {
//        // Arrange
//        ApiResponse<SendEmailResponse> apiResponse = new ApiResponse<>(new SendEmailResponse("42"));
//        when(emailAuthSender.sendSignup(Mockito.any(), Mockito.any())).thenReturn(apiResponse);
//
//        SendEmail email = new SendEmail();
//        email.setContent("Not all who wander are lost");
//        email.setFirstName("Jane");
//        email.setTo("evaristusadimonyemma@gmail.com");
//        email.setType(EmailType.SIGNUP);
//
//        // Act
//        ApiResponse<SendEmailResponse> actualSendResult = emailAuthSender.send(email);
//
//        // Assert
//        verify(emailAuthSender).sendSignup(Mockito.any(), Mockito.any());
//        assertSame(apiResponse, actualSendResult);
//    }
//
//    /**
//     * Method under test: {@link EmailAuthSender#send(SendEmail)}
//     */
//    @Test
//    void testSend2() {
//        // Arrange
//        SendEmail email = mock(SendEmail.class);
//        when(email.getTo()).thenThrow(new EmailException("Error"));
//        when(email.getType()).thenReturn(EmailType.SIGNUP);
//        doNothing().when(email).setContent(Mockito.any());
//        doNothing().when(email).setFirstName(Mockito.any());
//        doNothing().when(email).setTo(Mockito.any());
//        doNothing().when(email).setType(Mockito.any());
//        email.setContent("Not all who wander are lost");
//        email.setFirstName("Jane");
//        email.setTo("evaristusadimonyemma@gmail.com");
//        email.setType(EmailType.SIGNUP);
//
//        // Act and Assert
//        assertThrows(EmailException.class, () -> emailAuthSender.send(email));
//        verify(email).getTo();
//        verify(email).getType();
//        verify(email).setContent(Mockito.any());
//        verify(email).setFirstName(Mockito.any());
//        verify(email).setTo(Mockito.any());
//        verify(email).setType(Mockito.any());
//    }
//
//    /**
//     * Method under test: {@link EmailAuthSender#send(SendEmail)}
//     */
//    @Test
//    void testSend3() {
//        // Arrange
//        SendEmail email = mock(SendEmail.class);
//        when(email.getTo()).thenThrow(new EmailException("Error"));
//        when(email.getType()).thenReturn(EmailType.RESET);
//        doNothing().when(email).setContent(Mockito.any());
//        doNothing().when(email).setFirstName(Mockito.any());
//        doNothing().when(email).setTo(Mockito.any());
//        doNothing().when(email).setType(Mockito.any());
//        email.setContent("Not all who wander are lost");
//        email.setFirstName("Jane");
//        email.setTo("evaristusadimonyemma@gmail.com");
//        email.setType(EmailType.SIGNUP);
//
//        // Act and Assert
//        assertThrows(EmailException.class, () -> emailAuthSender.send(email));
//        verify(email).getTo();
//        verify(email).getType();
//        verify(email).setContent(Mockito.any());
//        verify(email).setFirstName(Mockito.any());
//        verify(email).setTo(Mockito.any());
//        verify(email).setType(Mockito.any());
//    }
}
