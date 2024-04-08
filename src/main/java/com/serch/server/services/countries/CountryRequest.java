package com.serch.server.services.countries;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class CountryRequest {
    private String country;
    private String state;
    private String city;
}
