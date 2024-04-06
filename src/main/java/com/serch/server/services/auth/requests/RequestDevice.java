package com.serch.server.services.auth.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class RequestDevice {
    private String id;
    private String name;

    @JsonProperty(value = "ip_address")
    private String ipAddress;
}
