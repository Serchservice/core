package com.serch.server.models.shop;

import com.serch.server.bases.BaseModel;
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
@Table(schema = "platform", name = "shop_services")
public class ShopService extends BaseModel {
    @Column(columnDefinition = "TEXT", nullable = false)
    private String service;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "shop_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "shop_id_fkey")
    )
    private Shop shop;
}
