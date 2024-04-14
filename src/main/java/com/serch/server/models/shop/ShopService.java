package com.serch.server.models.shop;

import com.serch.server.bases.BaseModel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The ShopService class represents a service offered by a shop in the platform.
 * It stores information such as the service name and the shop it belongs to.
 * <p></p>
 * Annotations:
 * <ul>
 *     <li>{@link Getter}</li>
 *     <li>{@link Setter}</li>
 *     <li>{@link AllArgsConstructor}</li>
 *     <li>{@link NoArgsConstructor}</li>
 *     <li>{@link Entity}</li>
 *     <li>{@link Table}</li>
 * </ul>
 * Relationships:
 * <ul>
 *     <li>Many-to-one with {@link Shop} as the shop.</li>
 * </ul>
 */
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
