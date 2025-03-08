package com.serch.server.domains.nearby.services.bcap.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.core.file.responses.FileUploadResponse;
import com.serch.server.domains.nearby.services.interest.responses.GoInterestResponse;
import lombok.Data;

import java.util.List;

@Data
public class GoBCapResponse {
    private String id;
    private String activity;
    private String name;
    private String description;
    private Double rating = 0.0;
    private String link;

    @JsonProperty("can_act_on_event")
    private Boolean canActOnEvent = false;

    @JsonProperty("rating_from_current_user")
    private Double ratingFromCurrentUser;

    @JsonProperty("can_rate")
    private Boolean canRate = false;

    @JsonProperty("can_comment")
    private Boolean canComment = false;

    @JsonProperty("has_comments")
    private Boolean hasComments = false;

    @JsonProperty("has_ratings")
    private Boolean hasRatings = false;

    @JsonProperty("total_ratings")
    private String totalRatings = "0";

    @JsonProperty("total_comments")
    private String totalComments = "0";

    @JsonProperty("is_created_by_current_user")
    private Boolean isCreatedByCurrentUser = false;

    private GoInterestResponse interest;
    private List<FileUploadResponse> files;
}