package com.serch.server.services.account.responses;

import com.serch.backend.platform.sharing.responses.GuestProfileResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
public class AccountResponse {
    private String emailAddress;
    private ProfileResponse profile;
    private GuestProfileResponse guest;
}
