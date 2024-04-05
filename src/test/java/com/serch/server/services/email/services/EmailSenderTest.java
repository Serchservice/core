package com.serch.server.services.email.services;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {EmailSender.class})
@ExtendWith(SpringExtension.class)
class EmailSenderTest {
    @Autowired
    private EmailSender emailSender;

    /**
     * Method under test:
     * {@link EmailSender#send(String, String, String, String, boolean)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testSend4() {
        // TODO: Complete this test.

        // Arrange and Act
        emailSender.send("jane.doe@example.org", "evaristusadimonyemma@gmail.com", "Hello from the Dreaming Spires",
                "Not all who wander are lost", true);
    }
}
