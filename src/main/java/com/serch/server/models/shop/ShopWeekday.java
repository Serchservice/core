package com.serch.server.models.shop;

import com.serch.server.annotations.SerchEnum;
import com.serch.server.bases.BaseModel;
import com.serch.server.enums.shop.Weekday;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

/**
 * The ShopWeekday class represents the opening and closing time of a shop in Serch platform.
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
@Table(schema = "platform", name = "shop_weekdays")
public class ShopWeekday extends BaseModel {
    @SerchEnum(message = "BusinessDay cannot be null")
    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Weekday day;

    @Column(nullable = false)
    private LocalTime opening;

    @Column(nullable = false)
    private LocalTime closing;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "shop_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "shop_id_fkey")
    )
    private Shop shop;
}