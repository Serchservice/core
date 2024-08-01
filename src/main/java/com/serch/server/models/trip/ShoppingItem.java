package com.serch.server.models.trip;

import com.serch.server.bases.BaseModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(schema = "trip", name = "shopping_items")
public class ShoppingItem extends BaseModel {
    @Column(nullable = false, updatable = false)
    private BigDecimal amount;

    @Column(columnDefinition = "TEXT", nullable = false, updatable = false)
    private String item;

    @Column(nullable = false, updatable = false)
    private Integer quantity = 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "trip_request_id",
            nullable = false,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "trip_request_shopping_item_id_fkey")
    )
    private TripInvite invite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "trip_id",
            nullable = false,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "trip_shopping_item_id_fkey")
    )
    private Trip trip;
}
