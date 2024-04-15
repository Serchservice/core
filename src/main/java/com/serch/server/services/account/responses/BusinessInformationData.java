package com.serch.server.services.account.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BusinessInformationData {
    @JsonProperty("business_name")
    private String businessName;

    @JsonProperty("business_description")
    private String businessDescription;

    @JsonProperty("business_address")
    private String businessAddress;

    @JsonProperty("business_logo")
    private String businessLogo;
}
