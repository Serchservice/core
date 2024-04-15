package com.serch.server.services.shared.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.services.auth.requests.RequestDevice;
import lombok.Data;

@Data
public class SwitchRequest {
    private String to;
    private String active;

    @JsonProperty("link_id")
    private String linkId;

    private String password;
    private RequestDevice device;
    private String platform;
}
