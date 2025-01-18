package com.serch.server.domains.auth.requests;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class RequestMFAChallenge {
    private String code;
    private RequestDevice device;
}
