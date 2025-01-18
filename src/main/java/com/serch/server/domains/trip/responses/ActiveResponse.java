package com.serch.server.domains.trip.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.enums.account.ProviderStatus;
import com.serch.server.enums.verified.VerificationStatus;
import com.serch.server.domains.account.responses.BusinessInformationData;
import com.serch.server.domains.account.responses.MoreProfileData;
import com.serch.server.domains.account.responses.SpecialtyResponse;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ActiveResponse implements Comparable<ActiveResponse> {
    private UUID id;
    private String name;
    private String avatar;
    private Double rating;
    private Double distance;
    private String image;
    private String category;
    private ProviderStatus status;

    @JsonProperty("distance_in_km")
    private String distanceInKm;

    @JsonProperty("verification_status")
    private VerificationStatus verificationStatus;

    private MoreProfileData more;
    private BusinessInformationData business;
    private List<SpecialtyResponse> specializations;

    @Override
    public int compareTo(ActiveResponse other) {
        return this.distance.compareTo(other.distance);
    }
}