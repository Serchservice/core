package com.serch.server.domains.nearby.services.drive.requests;

import lombok.Data;

@Data
public class NearbyDriveRequest {
    private String id;
    private String type;
    private String provider;
    private String option;
}