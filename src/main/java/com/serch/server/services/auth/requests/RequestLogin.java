package com.serch.server.services.auth.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class RequestLogin {
    @JsonProperty(value = "email_address")
    private String emailAddress;

    private String password;
    private RequestDevice device;
}
