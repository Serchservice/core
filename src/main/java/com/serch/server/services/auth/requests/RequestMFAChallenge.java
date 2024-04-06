package com.serch.server.services.auth.requests;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class RequestMFAChallenge {
    private String code;
    private String platform;
    private RequestDevice device;
}
