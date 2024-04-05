package com.serch.server.services.help.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class HelpAskRequest {
    @JsonProperty("email_address")
    private String emailAddress;

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("comment")
    private String comment;
}
