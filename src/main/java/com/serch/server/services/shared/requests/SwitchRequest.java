package com.serch.server.services.shared.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.services.auth.requests.RequestDevice;
import lombok.Data;

@Data
public class SwitchRequest {
    private String id;

    @JsonProperty("link_id")
    private String linkId;
    private RequestDevice device;
}