package com.serch.server.services.help.response;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.Data;

@Data
@Hidden
public class HelpCategoryResponse {
    private String category;
    private String image;
    private String id;
}
