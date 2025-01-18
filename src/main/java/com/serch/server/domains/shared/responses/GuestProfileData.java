package com.serch.server.domains.shared.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.enums.account.Gender;
import lombok.Data;

import java.util.List;

@Data
public class GuestProfileData {
    private String id;
    private Gender gender;
    private String avatar;
    private String name;
    private String status;

    @JsonProperty("joined_at")
    private String joinedAt;

    private List<SharedStatusData> statuses;
}