package com.serch.server.services.company.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.enums.account.SerchCategory;
import lombok.Data;

@Data
public class SpecialtyKeywordResponse {
    private Long id;
    private String special;
    private String difficulty;
    private String timeline;
    private SerchCategory category;

    @JsonProperty("price_range")
    private String priceRange;
}
