package com.serch.server.services.subscription.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.serch.server.enums.subscription.SubPlanType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(allowGetters = true, allowSetters = true, ignoreUnknown = true)
public class PlanChildResponse {
    private String id;
    private SubPlanType type;
    private String name;
    private Integer amount;
    private String discount;
    private String tag;
}
