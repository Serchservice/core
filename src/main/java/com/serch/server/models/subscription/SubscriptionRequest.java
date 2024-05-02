package com.serch.server.models.subscription;

import com.serch.server.bases.BaseModel;
import com.serch.server.models.auth.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * The SubscriptionRequest class represents a subscription request in the subscription schema.
 * It stores information such as reference, email address, size, and associated plan parent and child.
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
 *     <li>Many-to-one with {@link PlanParent} as the parent plan.</li>
 *     <li>Many-to-one with {@link PlanChild} as the child plan.</li>
 *     <li>One-to-one with {@link User} as the user.</li>
 *     <li>One-to-many with {@link SubscriptionRequestAssociate} as the associates tied to the request.</li>
 * </ul>
 */
@Getter
@Setter
@Entity
@Table(schema = "subscription", name = "requests")
public class SubscriptionRequest extends BaseModel {
    @Column(columnDefinition = "TEXT", nullable = false)
    private String reference;

    @Column(nullable = false)
    private Integer size;

    @Column(nullable = false)
    private String mode = "CARD";

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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "serch_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "plan_user_id_fkey")
    )
    private User user;

    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SubscriptionRequestAssociate> associates;
}