package com.serch.server.services.trip.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.enums.account.SerchCategory;
import lombok.Data;

@Data
public class TripShareRequest {
    private String trip;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("serch_category")
    private SerchCategory serchCategory;

    private String category;
    private Integer option;
}
