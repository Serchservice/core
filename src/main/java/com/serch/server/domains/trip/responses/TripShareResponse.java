package com.serch.server.domains.trip.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.enums.trip.TripStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class TripShareResponse extends TripActionResponse {
    private Long id;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("cancel_reason")
    private String cancelReason;

    @JsonProperty("is_provider")
    private Boolean isProvider = false;

    private String category;
    private TripStatus status;
    private String authentication;
    private UserResponse profile;
    private List<TripTimelineResponse> timelines;
    private MapViewResponse location;
}
