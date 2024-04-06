package com.serch.server.services.auth.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class RequestAuthSpecialty {
    @JsonProperty("email_address")
    private String emailAddress;

    private List<Long> specialties;
}
