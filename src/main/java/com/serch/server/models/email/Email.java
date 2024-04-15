package com.serch.server.models.email;

import lombok.Data;

/**
 * The Email class represents an email entity in the system.
 * It stores information about an email, including the content,
 * greeting, OTP, image header, centered status, and email address.
 * <p></p>
 * Annotations:
 * <ul>
 *     <li>{@link Data}</li>
 * </ul>
 */
@Data
public class Email {
    private String content;
    private String greeting;
    private String otp;
    private String imageHeader;
    private boolean isCentered;
    private String emailAddress;
}
