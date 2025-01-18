package com.serch.server.domains.account.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AccountResponse {
    private String id;
    private String category;
    private String name;
    private String avatar;

    @JsonProperty("category_image")
    private String categoryImage;

    @JsonProperty("link_id")
    private String linkId;

    @JsonProperty("email_address")
    private String emailAddress;
}