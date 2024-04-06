package com.serch.server.services.auth.requests;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class RequestResetPassword {
    private String emailAddress;
    private String password;
}
