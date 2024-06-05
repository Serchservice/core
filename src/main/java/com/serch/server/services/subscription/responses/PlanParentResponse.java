package com.serch.server.services.subscription.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(allowGetters = true, allowSetters = true, ignoreUnknown = true)
public class PlanParentResponse {
    private String id;
    private String type;
    private String description;
    private String image;
    private String color;
    private String duration;
    private String amount;
    private ArrayList<String> benefits;
    private ArrayList<PlanChildResponse> children;
}
