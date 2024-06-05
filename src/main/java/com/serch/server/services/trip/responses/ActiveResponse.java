package com.serch.server.services.trip.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.enums.trip.TripStatus;
import com.serch.server.enums.verified.VerificationStatus;
import com.serch.server.services.business.responses.BusinessInformationData;
import com.serch.server.services.account.responses.MoreProfileData;
import com.serch.server.services.account.responses.SpecialtyResponse;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ActiveResponse {
    private UUID id;
    private String name;
    private String avatar;
    private Double rating;
    private Double distance;
    private String image;
    private String category;
    private TripStatus status;

    @JsonProperty("distance_in_km")
    private String distanceInKm;

    @JsonProperty("verification_status")
    private VerificationStatus verificationStatus;

    private MoreProfileData more;
    private BusinessInformationData business;
    private List<SpecialtyResponse> specializations;
}