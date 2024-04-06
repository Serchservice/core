package com.serch.server.models.email;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.enums.email.EmailType;
import lombok.Data;

@Data
public class SendEmail {
    private String to;
    private String content;
    private EmailType type;

    @JsonProperty("first_name")
    private String firstName;
}
