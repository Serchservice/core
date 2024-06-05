package com.serch.server.models.subscription;

import com.serch.server.bases.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * The SubscriptionAuth class represents the authorization details of a subscription in the subscription schema.
 * It stores information such as card details, email address, bank details, and authorization signature.
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
 *     <li>One-to-one with {@link Subscription} as the subscription.</li>
 * </ul>
 */
@Getter
@Setter
@Entity
@Table(schema = "subscription", name = "authorization")
public class SubscriptionAuth extends BaseEntity {
    @Column(nullable = false, columnDefinition = "TEXT")
    private String code;

    @Column(nullable = false, columnDefinition = "TEXT", name = "card_type")
    private String cardType;

    @Column(nullable = false, columnDefinition = "TEXT", name = "last_4")
    private String last4;

    @Column(nullable = false, columnDefinition = "TEXT", name = "exp_month")
    private String expMonth;

    @Column(nullable = false, columnDefinition = "TEXT", name = "exp_year")
    private String expYear;

    @Column(nullable = false, columnDefinition = "TEXT", name = "email_address", updatable = false)
    private String emailAddress;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String bin;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String bank;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String channel;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String signature;

    @Column(columnDefinition = "TEXT", name = "country_code")
    private String countryCode;

    @Column(nullable = false)
    private Boolean reusable = false;

    @Column(columnDefinition = "TEXT", name = "account_name")
    private String accountName;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "subscription_id",
            referencedColumnName = "id",
            nullable = false,
            updatable = false,
            foreignKey = @ForeignKey(name = "subscription_id_fkey")
    )
    private Subscription subscription;
}
