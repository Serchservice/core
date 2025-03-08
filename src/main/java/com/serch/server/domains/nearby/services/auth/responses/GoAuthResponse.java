package com.serch.server.domains.nearby.services.auth.responses;

import com.serch.server.domains.nearby.bases.GoBaseUserResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class GoAuthResponse extends GoBaseUserResponse {
    private String id;
    private String session;
}