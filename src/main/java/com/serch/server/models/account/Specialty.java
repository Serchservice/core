package com.serch.server.models.account;

import com.serch.server.bases.BaseModel;
import com.serch.server.models.company.ServiceKeyword;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = "account", name = "specializations")
public class Specialty extends BaseModel {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "service_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "special_service_id_fkey")
    )
    private ServiceKeyword service;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "serch_id",
            referencedColumnName = "serch_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "special_serch_id_fkey")
    )
    private Profile profile;
}
