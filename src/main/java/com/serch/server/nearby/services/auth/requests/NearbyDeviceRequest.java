package com.serch.server.nearby.services.auth.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.domains.auth.requests.RequestDevice;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class NearbyDeviceRequest extends RequestDevice {
    private UUID id;

    @JsonProperty("fcm_token")
    private String fcmToken;
}