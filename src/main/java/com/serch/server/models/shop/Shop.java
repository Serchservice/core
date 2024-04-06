package com.serch.server.models.shop;

import com.serch.server.annotations.SerchEnum;
import com.serch.server.bases.BaseUser;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.enums.shop.ShopStatus;
import com.serch.server.models.account.BusinessProfile;
import com.serch.server.models.account.Profile;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = "platform", name = "shops")
public class Shop extends BaseUser {
    @Column(columnDefinition = "TEXT", nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String address;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private Double rating;

    @Column(name = "phone_number", columnDefinition = "TEXT", nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "Category must be of SerchCategory")
    private SerchCategory category;

    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "Status must be of ProfileState")
    private ShopStatus status = ShopStatus.OFFLINE;

    @Column(name = "place", columnDefinition = "TEXT", nullable = false)
    private String place;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "profile_id",
            referencedColumnName = "serch_id",
            foreignKey = @ForeignKey(name = "shop_profile_id_fkey")
    )
    private Profile profile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "business_id",
            referencedColumnName = "serch_id",
            foreignKey = @ForeignKey(name = "shop_business_id_fkey")
    )
    private BusinessProfile business;

    @OneToMany(mappedBy = "shop", fetch = FetchType.LAZY)
    private List<ShopService> services;
}
