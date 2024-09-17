package com.serch.server.models.company;

import com.serch.server.bases.BaseModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This class is the record for any complaints about Serch platform or website, even product.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = "company", name = "service_suggestions")
public class ServiceSuggest extends BaseModel {
    @Column(name = "service", nullable = false, columnDefinition = "TEXT")
    @NotEmpty(message = "Service cannot be empty")
    private String service;

    @Column(name = "platform", nullable = false, columnDefinition = "TEXT")
    @NotEmpty(message = "Service cannot be empty")
    private String platform;
}