package com.serch.server.services.shop.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

/**
 * DTO for {@link com.serch.server.models.shop.ShopService}
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShopServiceResponse implements Serializable {
    private Long id;
    private String service;
}