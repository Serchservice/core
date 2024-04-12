package com.serch.server.services.shared.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SharedAccountResponse {
    @JsonProperty("link_id")
    private String linkId;

    private Integer count;
    private String link;
    private String id;
    private String mode;
    private String name;
    private String avatar;

    @JsonProperty("email_address")
    private String emailAddress;
}
