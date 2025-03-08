package com.serch.server.domains.nearby.services.account.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.bases.BaseLocation;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class GoLocationResponse extends BaseLocation {
    private String id;
    private String country;
    private String state;
    private String city;

    @JsonProperty("local_government_area")
    private String localGovernmentArea;

    @JsonProperty("street_number")
    private String streetNumber;

    @JsonProperty("street_name")
    private String streetName;
}
