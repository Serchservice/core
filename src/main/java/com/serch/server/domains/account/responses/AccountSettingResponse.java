package com.serch.server.domains.account.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.enums.account.Gender;
import lombok.Data;

@Data
public class AccountSettingResponse {
    private Gender gender;

    @JsonProperty("show_only_verified")
    private Boolean showOnlyVerified;

    @JsonProperty("show_only_certified")
    private Boolean showOnlyCertified;
}