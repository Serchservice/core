package com.serch.server.services.email.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.services.email.EmailType;
import lombok.Data;

@Data
public class SendEmail {
    private String to;
    private String content;
    private EmailType type;

    @JsonProperty("first_name")
    private String firstName;
}
