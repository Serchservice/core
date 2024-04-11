package com.serch.server.services.account.responses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.enums.verified.VerificationStatus;
import com.serch.server.services.auth.responses.SpecialtyResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
public class ProfileResponse {
    private UUID id;
    private SerchCategory category;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("email_address")
    private String emailAddress;

    private String gender;
    private String status;

    @JsonProperty("referral_link")
    private String referLink;

    @JsonProperty("referral_code")
    private String referralCode;

    @JsonProperty("phone_info")
    private PhoneInformationRequest phoneInfo;

    private String avatar;
    private String certificate;

    @JsonProperty("last_seen")
    private String lastSeen;

    @JsonProperty("number_of_rating")
    private Integer numberOfRating;

    private Double rating;

    @JsonProperty("total_service_trips")
    private Integer totalServiceTrips;

    @JsonProperty("total_shared")
    private Integer totalShared;

    @JsonProperty("is_enabled")
    private Boolean isEnabled;

    @JsonProperty("is_non_locked")
    private Boolean isNonLocked;

    @JsonProperty("is_non_expired")
    private Boolean isNonExpired;

    @JsonProperty("trip_status")
    private String tripStatus;

    @JsonProperty("verification_status")
    private VerificationStatus verificationStatus;

    private List<SpecialtyResponse> specializations;

    @JsonIgnore
    private LocalDateTime createdAt;
}
