package com.serch.server.admin.services.scopes.countries_in_serch.requests;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AddCountryRequest {
    @NotEmpty(message = "Country name cannot be empty")
    private String country;

    @NotEmpty(message = "Emoji cannot be empty")
    private String emoji;

    @NotEmpty(message = "Flag cannot be empty")
    private String flag;

    @NotNull(message = "Dial code cannot be null")
    private Integer dialCode;

    @NotEmpty(message = "Code cannot be empty")
    @Size(min = 2, max = 3, message = "Code must be between 2 and 3 characters")
    private String code;
}