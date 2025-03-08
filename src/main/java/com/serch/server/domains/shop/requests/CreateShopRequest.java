package com.serch.server.domains.shop.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.core.file.requests.FileUploadRequest;
import lombok.Data;

import java.util.List;

@Data
public class CreateShopRequest {
    private String name;
    private SerchCategory category;

    @JsonProperty("phone_number")
    private String phoneNumber;

    private String address;
    private Double latitude;
    private Double longitude;
    private FileUploadRequest upload;
    private List<ShopWeekdayRequest> weekdays;
    private List<String> services;
}