package com.serch.server.services.shared.responses;

import lombok.Data;

import java.util.UUID;

@Data
public class SharedProfileData {
    private UUID id;
    private String name;
    private String avatar;
    private String category;
    private Double rating;
}
