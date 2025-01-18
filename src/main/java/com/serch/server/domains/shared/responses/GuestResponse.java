package com.serch.server.domains.shared.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class GuestResponse {
    private String id;
    private String gender;
    private String avatar;

    @JsonProperty("email_address")
    private String emailAddress;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("joined_at")
    private String joinedAt;

    private Boolean confirmed;
    private SharedLinkData link;
    private List<SharedStatusData> statuses;
}