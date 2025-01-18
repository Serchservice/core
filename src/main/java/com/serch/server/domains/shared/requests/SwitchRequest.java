package com.serch.server.domains.shared.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.domains.auth.requests.RequestDevice;
import lombok.Data;

@Data
public class SwitchRequest {
    private String id;

    @JsonProperty("link_id")
    private String linkId;
    private RequestDevice device;
}