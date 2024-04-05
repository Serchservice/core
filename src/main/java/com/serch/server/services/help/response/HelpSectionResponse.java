package com.serch.server.services.help.response;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.Data;

@Data
@Hidden
public class HelpSectionResponse {
    private String section;
    private String image;
    private String id;
}
