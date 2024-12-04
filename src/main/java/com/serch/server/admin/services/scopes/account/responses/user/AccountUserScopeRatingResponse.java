package com.serch.server.admin.services.scopes.account.responses.user;

import com.serch.server.admin.enums.EventType;
import com.serch.server.admin.services.responses.CommonProfileResponse;
import com.serch.server.services.rating.responses.RatingChartResponse;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AccountUserScopeRatingResponse {
    private List<RatingChartResponse> chart = new ArrayList<>();
    private List<Rating> ratings = new ArrayList<>();

    @Data
    public static class Rating {
        private Long id;
        private Double rating;
        private String comment;
        private String event;
        private EventType type;
        private CommonProfileResponse profile;
    }
}