package com.serch.server.domains.account.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SpecialtyResponse {
    private Long id;
    private String special;
    private String category;
    private String image;
    private String avatar;
}