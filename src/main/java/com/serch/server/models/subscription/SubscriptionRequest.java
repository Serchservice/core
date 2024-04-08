package com.serch.server.models.subscription;

import com.serch.server.bases.BaseModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(schema = "subscription", name = "requests")
public class SubscriptionRequest extends BaseModel {
    @Column(columnDefinition = "TEXT", nullable = false)
    private String reference;

    @Column(name = "email_address", columnDefinition = "TEXT", nullable = false)
    private String emailAddress;

    @Column(nullable = false)
    private Integer size;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "plan_parent_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "plan_parent_id_fkey")
    )
    private PlanParent parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "plan_child_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "plan_child_id_fkey")
    )
    private PlanChild child;
}
