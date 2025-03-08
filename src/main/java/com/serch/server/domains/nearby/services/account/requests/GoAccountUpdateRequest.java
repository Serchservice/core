package com.serch.server.domains.nearby.services.account.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.core.file.requests.FileUploadRequest;
import com.serch.server.core.location.responses.Address;
import com.serch.server.domains.nearby.bases.GoBaseUserResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class GoAccountUpdateRequest extends GoBaseUserResponse {
    private String timezone;

    @JsonProperty("fcm_token")
    private String fcmToken;

    @JsonProperty("search_radius")
    private Double searchRadius;

    private Address address;
    private FileUploadRequest upload;
}