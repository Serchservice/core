package com.serch.server.services.email;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.resend.services.emails.model.SendEmailResponse;
import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.email.EmailType;
import com.serch.server.models.email.SendEmail;
import com.serch.server.services.email.services.EmailAuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {EmailController.class})
@ExtendWith(SpringExtension.class)
class EmailControllerTest {
    @Autowired
    private EmailController emailController;

    @MockBean
    private EmailAuthService emailService;

    /**
     * Method under test: {@link EmailController#send(SendEmail)}
     */
    @Test
    void testSend() throws Exception {
        // Arrange
        when(emailService.send(Mockito.any())).thenReturn(new ApiResponse<>(new SendEmailResponse("42")));

        SendEmail sendEmail = new SendEmail();
        sendEmail.setContent("Not all who wander are lost");
        sendEmail.setFirstName("Jane");
        sendEmail.setTo("evaristusadimonyemma@gmail.com");
        sendEmail.setType(EmailType.SIGNUP);
        String content = (new ObjectMapper()).writeValueAsString(sendEmail);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/email/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);

        // Act and Assert
        MockMvcBuilders.standaloneSetup(emailController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content()
                        .string("{\"status\":\"OK\",\"code\":200,\"message\":\"Successful\",\"data\":{\"id\":\"42\"}}"));
    }
}
