package com.serch.server.services.transaction.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AssociateTransactionData {
    private String name;
    private String category;
    private Double rating;
    private String avatar;
    private String image;
}
