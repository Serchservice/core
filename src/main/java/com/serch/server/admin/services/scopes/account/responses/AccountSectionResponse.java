package com.serch.server.admin.services.scopes.account.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.admin.services.responses.CommonProfileResponse;
import lombok.Data;

import java.util.List;

@Data
public class AccountSectionResponse {
    private List<CommonProfileResponse> accounts;
    private Long total;

    @JsonProperty("total_pages")
    private Integer totalPages;
}