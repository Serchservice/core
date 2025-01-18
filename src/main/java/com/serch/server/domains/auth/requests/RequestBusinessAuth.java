package com.serch.server.domains.auth.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class RequestBusinessAuth {
    @JsonProperty("email_address")
    private String emailAddress;
    private RequestDevice device;
}
