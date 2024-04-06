package com.serch.server.models.email;

import lombok.Data;

@Data
public class Email {
    private String content;
    private String greeting;
    private String otp;
    private String imageHeader;
    private boolean isCentered;
    private String emailAddress;
}
