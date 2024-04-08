package com.serch.server.models.subscription;

import com.serch.server.bases.BaseModel;
import com.serch.server.models.account.BusinessProfile;
import com.serch.server.models.account.Profile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(schema = "subscription", name = "associate_subscriptions")
public class SubscriptionAssociate extends BaseModel {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "associate_id",
            referencedColumnName = "serch_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "shop_profile_id_fkey")
    )
    private Profile profile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "business_id",
            referencedColumnName = "serch_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "shop_business_id_fkey")
    )
    private BusinessProfile business;
}
