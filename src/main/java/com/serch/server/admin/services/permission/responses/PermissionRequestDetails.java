package com.serch.server.admin.services.permission.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PermissionRequestDetails {
    private String admin;
    private String name;
    private String granted;

    @JsonProperty("updated_by")
    private String updatedBy;

    @JsonProperty("updated_by_name")
    private String updatedByName;
}
