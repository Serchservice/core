package com.serch.server.services.help.response;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.Data;

import java.util.UUID;

@Data
@Hidden
public class HelpResponse {
    private String question;
    private String answer;
    private UUID id;
}
