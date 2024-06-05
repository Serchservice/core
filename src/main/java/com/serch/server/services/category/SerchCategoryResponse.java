package com.serch.server.services.category;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.services.account.responses.SpecialtyResponse;
import lombok.Data;

import java.util.List;

@Data
public class SerchCategoryResponse {
    private String type;
    private String image;
    private SerchCategory category;
    private String information;

    @JsonProperty("can_drive")
    private Boolean canDrive;

    @JsonProperty("can_search_skill")
    private Boolean canSearchSkill;
}