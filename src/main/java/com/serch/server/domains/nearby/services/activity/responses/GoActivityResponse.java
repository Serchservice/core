package com.serch.server.domains.nearby.services.activity.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.core.file.responses.FileUploadResponse;
import com.serch.server.enums.trip.TripStatus;
import com.serch.server.domains.nearby.bases.GoBaseUserResponse;
import com.serch.server.domains.nearby.services.account.responses.GoLocationResponse;
import com.serch.server.domains.nearby.services.interest.responses.GoInterestResponse;
import lombok.Data;

import java.util.List;

@Data
public class GoActivityResponse {
    private String id;
    private String bcap;
    private String name;
    private String description;
    private String startTime;
    private String endTime;
    private TripStatus status;
    private String timestamp;
    private Double rating = 0.0;
    private String link;

    @JsonProperty("has_responded")
    private Boolean hasResponded;

    @JsonProperty("has_similar_activities_from_other_creators")
    private Boolean hasSimilarActivitiesFromOtherCreators;

    @JsonProperty("has_similar_activities_from_creator")
    private Boolean hasSimilarActivitiesFromCreator;

    @JsonProperty("is_created_by_current_user")
    private Boolean isCreatedByCurrentUser;

    @JsonProperty("can_rate")
    private Boolean canRate;

    @JsonProperty("can_comment")
    private Boolean canComment;

    @JsonProperty("has_comments")
    private Boolean hasComments = false;

    @JsonProperty("has_ratings")
    private Boolean hasRatings = false;

    @JsonProperty("total_ratings")
    private String totalRatings = "0";

    @JsonProperty("total_comments")
    private String totalComments = "0";

    private GoLocationResponse location;
    private GoBaseUserResponse user;
    private GoActivityPollResponse poll;
    private GoInterestResponse interest;
    private List<FileUploadResponse> images;

    @JsonProperty("attending_users")
    private List<GoBaseUserResponse> attendingUsers;
}