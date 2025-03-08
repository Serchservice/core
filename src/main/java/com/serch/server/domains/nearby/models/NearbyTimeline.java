package com.serch.server.domains.nearby.models;

import com.serch.server.bases.BaseModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "timeline", schema = "nearby")
public class NearbyTimeline extends BaseModel {
    @Column(columnDefinition = "TEXT", nullable = false)
    private String option;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "category_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "nearby_category_id_fkey")
    )
    private NearbyCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "shop_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "nearby_shop_id_fkey")
    )
    private NearbyShop shop;
}