package com.serch.server.services.shared.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.enums.shared.UseStatus;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SharedPricingData {
    private BigDecimal user;
    private BigDecimal provider;
    private BigDecimal total;
    private String more;
    private UseStatus status;
    private String label;
    private String name;
    private String avatar;

    @JsonProperty("joined_at")
    private String joinedAt;
}
