package com.serch.server.models.subscription;

import com.serch.server.annotations.SerchEnum;
import com.serch.server.bases.BaseModel;
import com.serch.server.enums.subscription.PlanStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * The SubscriptionInvoice class represents the invoice details of a subscription in the subscription schema.
 * It stores information such as invoice size, amount, plan, mode of payment, and reference.
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
 *     <li>Many-to-one with {@link Subscription} as the subscription.</li>
 *     <li>One-to-many with {@link SubscriptionAssociate} as associates.</li>
 * </ul>
 */
@Getter
@Setter
@Entity
@Table(schema = "subscription", name = "invoices")
public class SubscriptionInvoice extends BaseModel {
    @Column(nullable = false, updatable = false)
    private Integer size = 1;

    @Column(nullable = false, updatable = false)
    private String amount;

    @Column(nullable = false, updatable = false)
    private String plan;

    @Column(nullable = false, updatable = false)
    private String mode;

    @Column(nullable = false, updatable = false)
    private String reference;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "PlanType must be an enum")
    private PlanStatus status = PlanStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "subscription",
            referencedColumnName = "id",
            nullable = false,
            updatable = false,
            foreignKey = @ForeignKey(name = "subscription_invoice_id_fkey")
    )
    private Subscription subscription;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "invoice")
    private List<SubscriptionAssociate> associates;
}
