package com.serch.server.domains.nearby.services.auth.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.domains.auth.requests.RequestDevice;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class NearbyDeviceRequest extends RequestDevice {
    private String id;

    @JsonProperty("fcm_token")
    private String fcmToken;
}