package com.serch.server.services.shared.responses;

import com.serch.server.enums.shared.UseStatus;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SharedLinkData {
    private String id;
    private UseStatus status;
    private String link;
    private BigDecimal amount;
}
