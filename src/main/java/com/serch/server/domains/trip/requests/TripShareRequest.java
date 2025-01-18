package com.serch.server.domains.trip.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.enums.trip.TripShareOption;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

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
    private List<String> filters = new ArrayList<>();
    private TripShareOption option = TripShareOption.ONLINE;

    public boolean isOnline() {
        return option == TripShareOption.ONLINE;
    }
}
