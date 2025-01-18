package com.serch.server.domains.auth.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class RequestDevice {
    private String device;
    private String name;
    private String host;
    private String platform;

    @JsonProperty("operating_system")
    private String operatingSystem;

    @JsonProperty("operating_system_version")
    private String operatingSystemVersion;

    @JsonProperty("local_host")
    private String localHost;

    @JsonProperty("ip_address")
    private String ipAddress;
}