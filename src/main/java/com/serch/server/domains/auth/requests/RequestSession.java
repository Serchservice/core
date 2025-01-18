package com.serch.server.domains.auth.requests;

import com.serch.server.enums.auth.AuthMethod;
import com.serch.server.models.auth.User;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class RequestSession {
    private User user;
    private RequestDevice device;
    private AuthMethod method;
}