package com.serch.server.services.auth.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class RequestPasswordChange {
    @JsonProperty("new_password")
    private String newPassword;

    @JsonProperty("old_password")
    private String oldPassword;

    private String platform;
    private RequestDevice device;
}
