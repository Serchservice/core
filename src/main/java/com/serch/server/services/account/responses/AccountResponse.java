package com.serch.server.services.account.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.services.shared.responses.SharedAccountResponse;
import lombok.Data;

import java.util.List;

@Data
public class AccountResponse {
    private String id;
    private String mode;
    private String name;
    private String avatar;

    @JsonProperty("email_address")
    private String emailAddress;
    List<SharedAccountResponse> guests;
}
