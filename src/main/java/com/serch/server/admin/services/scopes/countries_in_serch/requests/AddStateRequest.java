package com.serch.server.admin.services.scopes.countries_in_serch.requests;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class AddStateRequest {
    private Long id;
    @NotEmpty(message = "State cannot be empty")
    private String state;
}
