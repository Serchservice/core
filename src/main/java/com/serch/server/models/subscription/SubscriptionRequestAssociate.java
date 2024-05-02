package com.serch.server.models.subscription;

import com.serch.server.bases.BaseModel;
import com.serch.server.models.account.Profile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * The SubscriptionRequestAssociate class represents an association between a request and a subscription associate
 * in the subscription schema.
 * It stores information about the associated provider and request.
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
 *     <li>Many-to-one with {@link SubscriptionRequest} as the request.</li>
 *     <li>Many-to-one with {@link Profile} as the profile.</li>
 * </ul>
 */
@Getter
@Setter
@Entity
@Table(schema = "subscription", name = "request_associates")
public class SubscriptionRequestAssociate extends BaseModel {
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
            name = "invoice_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "sub_request_id_fkey")
    )
    private SubscriptionRequest request;
}