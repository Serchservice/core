package com.serch.server.models.subscription;

import com.serch.server.bases.BaseModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(schema = "subscription", name = "invoices")
public class SubscriptionInvoice extends BaseModel {
    @Column(nullable = false)
    private Integer size = 0;

    @Column(nullable = false)
    private String amount;

    @Column(nullable = false)
    private String plan;

    @Column(nullable = false)
    private String mode;

    private String reference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "subscription_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "subscription_invoice_id_fkey")
    )
    private Subscription subscription;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "invoice")
    private List<SubscriptionAssociate> associates;
}
