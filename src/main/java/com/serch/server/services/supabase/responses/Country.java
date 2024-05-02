package com.serch.server.services.supabase.responses;

import lombok.Data;

@Data
public class Country {
    private String name;
    private String flag;
    private String code;
    private Long dialCode;
    private Long minLength;
    private Long maxLength;
    private String image;
}
