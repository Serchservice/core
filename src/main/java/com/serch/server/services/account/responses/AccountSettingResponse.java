package com.serch.server.services.account.responses;

import com.serch.server.enums.account.Gender;
import lombok.Data;

@Data
public class AccountSettingResponse {
    private Gender gender;
}
