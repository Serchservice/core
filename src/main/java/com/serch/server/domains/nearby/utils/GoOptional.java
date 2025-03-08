package com.serch.server.domains.nearby.utils;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class GoOptional {
    private Double lng;
    private Double lat;
    private Double rad = 5000.0;
    private List<Long> interests = new ArrayList<>();
    private UUID user;

    public GoOptional(double lat, double lng, double rad) {
        this.lng = lng;
        this.lat = lat;
        this.rad = rad;
    }
}