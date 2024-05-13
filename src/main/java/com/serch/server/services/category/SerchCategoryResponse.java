package com.serch.server.services.category;

import com.serch.server.enums.account.SerchCategory;
import com.serch.server.services.company.responses.SpecialtyKeywordResponse;
import lombok.Data;

import java.util.List;

@Data
public class SerchCategoryResponse {
    private String type;
    private String image;
    private SerchCategory category;
    private String information;
    private List<SpecialtyKeywordResponse> specialties;
}