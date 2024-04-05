package com.serch.server.services.help.response;

import lombok.Data;

@Data
public class HelpSearchResponse {
    private String question;
    private String section;
    private String category;
    private String link;
    private String image;
}
