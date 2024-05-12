package com.serch.server.services.shop.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.services.supabase.requests.FileUploadRequest;
import lombok.Data;

@Data
public class UpdateShopRequest {
    private String shop;
    private String name;
    private SerchCategory category;

    @JsonProperty("phone_number")
    private String phoneNumber;

    private String address;
    private Double latitude;
    private Double longitude;
}
