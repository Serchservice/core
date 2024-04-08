package com.serch.server.services.company.responses;

import com.serch.server.enums.company.ProductType;
import lombok.Data;

@Data
public class ProductResponse {
    private String id;
    private String name;
    private ProductType type;
}
