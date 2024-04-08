package com.serch.server.models.subscription;

import com.serch.server.bases.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    @Column(nullable = false, columnDefinition = "TEXT", name = "email_address")
    private String emailAddress;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String bin;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String bank;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String channel;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String signature;

    @Column(nullable = false, columnDefinition = "TEXT", name = "country_code")
    private String countryCode;

    @Column(nullable = false)
    private Boolean reusable = false;

    @Column(nullable = false, columnDefinition = "TEXT", name = "account_name")
    private String accountName;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "subscription_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "subscription_id_fkey")
    )
    private Subscription subscription;
}
