package com.serch.server.services.shop.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.serch.server.models.shop.ShopSpecialty;
import lombok.Data;

import java.io.Serializable;

/**
 * DTO for {@link ShopSpecialty}
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShopServiceResponse implements Serializable {
    private Long id;
    private String service;
}