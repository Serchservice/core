package com.serch.server.services.trip.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.enums.trip.TripStatus;
import com.serch.server.enums.verified.VerificationStatus;
import com.serch.server.services.account.responses.BusinessInformationData;
import com.serch.server.services.account.responses.MoreProfileData;
import com.serch.server.services.company.responses.SpecialtyKeywordResponse;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ActiveResponse {
    private UUID id;
    private String name;
    private String avatar;
    private String distance;
    private Double rating;
    private TripStatus status;

    @JsonProperty("verification_status")
    private VerificationStatus verificationStatus;

    private MoreProfileData more;
    private BusinessInformationData business;
    private List<SpecialtyKeywordResponse> specializations;
}