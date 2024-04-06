package com.serch.server.services.auth.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class RequestAuth {
    @JsonProperty("email_address")
    private String emailAddress;

    private String platform;
    private RequestDevice device;
}
