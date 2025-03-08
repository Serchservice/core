package com.serch.server.domains.nearby.services.activity.responses;

import com.serch.server.enums.trip.TripStatus;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class GoActivityLifecycleResponse {
    private String id;
    private Long interest;
    private String title;
    private String message;
    private String summary;
    private TripStatus status;
    private String username;
    private String contact;
    private Double latitude;
    private Double longitude;
    private String address;
}