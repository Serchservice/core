package com.serch.server.models.email;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.enums.email.EmailType;
import lombok.Data;

/**
 * The SendEmail class represents an email to be sent in the system.
 * It contains information about the recipient, email content, email type, and recipient's first name.
 * <p></p>
 * Annotations:
 * <ul>
 *     <li>{@link Data}</li>
 * </ul>
 */
@Data
public class SendEmail {
    private String to;
    private String content;
    private EmailType type;
    private String primary;
    private String secondary;

    @JsonProperty("first_name")
    private String firstName;
}
