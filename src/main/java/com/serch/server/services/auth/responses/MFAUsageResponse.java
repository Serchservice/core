package com.serch.server.services.auth.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MFAUsageResponse {
    private Integer used;
    private Integer unused;
    private Integer total;
}
