package com.serch.server.models.shop;

import com.serch.server.annotations.CoreID;
import com.serch.server.annotations.SerchEnum;
import com.serch.server.bases.BaseDateTime;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.enums.shop.ShopStatus;
import com.serch.server.models.auth.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * The Shop class represents a shop in the platform.
 * It stores information such as the name, address, location coordinates, phone number, category, status, and associated user.
 * <p></p>
 * Relationships:
 * <ul>
 *     <li>One-to-one with {@link User} as the user.</li>
 *     <li>One-to-many with {@link ShopSpecialty} as the services.</li>
 * </ul>
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = "platform", name = "shops")
public class Shop extends BaseDateTime {
    @Id
    @CoreID(name = "shop_generator", prefix = "SSP")
    @Column(nullable = false, columnDefinition = "TEXT")
    @GeneratedValue(generator = "shop_gen")
    private String id;

    @Column(columnDefinition = "TEXT", nullable = false)
    @NotNull(message = "Shop name cannot be null")
    private String name;

    @Column(columnDefinition = "TEXT", nullable = false)
    @NotNull(message = "Shop logo cannot be null")
    private String logo;

    @Column(columnDefinition = "TEXT", nullable = false)
    @NotNull(message = "Address cannot be null")
    private String address;

    @Column(nullable = false)
    @NotNull(message = "Latitude cannot be null")
    private Double latitude;

    @Column(nullable = false)
    @NotNull(message = "Longitude cannot be null")
    private Double longitude;

    @Column(name = "phone_number", columnDefinition = "TEXT", nullable = false)
    @NotNull(message = "Phone number cannot be null")
    private String phoneNumber;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "Category must be of SerchCategory")
    private SerchCategory category;

    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "Status must be of ProfileState")
    private ShopStatus status = ShopStatus.OPEN;

    @Column(name = "rating", nullable = false)
    private Double rating = 5.0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "serch_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "shop_user_id_fkey")
    )
    private User user;

    @OneToMany(mappedBy = "shop", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ShopSpecialty> services;

    @OneToMany(mappedBy = "shop", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ShopWeekday> weekdays;

    public boolean isOpen() {
        return getStatus() == ShopStatus.OPEN;
    }
}
