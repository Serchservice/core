package com.serch.server.models.auth.incomplete;

import com.serch.server.bases.BaseModel;
import com.serch.server.models.company.SpecialtyService;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@Entity
@Table(schema = "identity", name = "incomplete_specializations")
public class IncompleteSpecialty extends BaseModel {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "service_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "special_service_id_fkey")
    )
    @ToString.Exclude
    private SpecialtyService service;

    @ManyToOne(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JoinColumn(
            name = "incomplete_email",
            referencedColumnName = "email_address",
            nullable = false,
            foreignKey = @ForeignKey(name = "special_email_fkey")
    )
    @ToString.Exclude
    private Incomplete incomplete;
}