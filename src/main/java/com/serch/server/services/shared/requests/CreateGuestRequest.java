package com.serch.server.services.shared.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.annotations.SerchEnum;
import com.serch.server.enums.account.Gender;
import com.serch.server.services.auth.requests.RequestDevice;
import com.serch.server.services.supabase.requests.FileUploadRequest;
import lombok.Data;

@Data
public class CreateGuestRequest {
    @JsonProperty("email_address")
    private String emailAddress;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("fcm_token")
    private String fcmToken;

    @SerchEnum(message = "Gender must be an enum")
    private Gender gender;

    private FileUploadRequest upload;
    private String link;

    @JsonProperty("link_id")
    private String linkId;
    private RequestDevice device;
}