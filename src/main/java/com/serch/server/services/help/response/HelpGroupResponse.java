package com.serch.server.services.help.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.Data;

import java.util.List;

@Data
@Hidden
public class HelpGroupResponse {
    private String group;
    private String key;

    @JsonProperty("help")
    private List<HelpResponse> helpResponses;
}
