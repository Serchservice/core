package com.serch.server.models.subscription;

import com.serch.server.bases.BaseModel;
import com.serch.server.models.business.BusinessProfile;
import com.serch.server.models.account.Profile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * The SubscriptionAssociate class represents an association between a profile, a business profile, and a subscription invoice
 * in the subscription schema. It stores information about the associated profile, business profile, and subscription invoice.
 * <p></p>
 * Annotations:
 * <ul>
 *     <li>{@link Getter}</li>
 *     <li>{@link Setter}</li>
 *     <li>{@link Entity}</li>
 *     <li>{@link Table}</li>
 * </ul>
 * Relationships:
 * <ul>
 *     <li>Many-to-one with {@link Profile} as the profile.</li>
 *     <li>Many-to-one with {@link BusinessProfile} as the business.</li>
 *     <li>Many-to-one with {@link SubscriptionInvoice} as the invoice.</li>
 * </ul>
 */
@Getter
@Setter
@Entity
@Table(schema = "subscription", name = "associate_subscriptions")
public class SubscriptionAssociate extends BaseModel {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "associate_id",
            referencedColumnName = "serch_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "sub_profile_id_fkey")
    )
    private Profile profile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "business_id",
            referencedColumnName = "serch_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "sub_business_id_fkey")
    )
    private BusinessProfile business;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "invoice_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "sub_invoice_id_fkey")
    )
    private SubscriptionInvoice invoice;
}
