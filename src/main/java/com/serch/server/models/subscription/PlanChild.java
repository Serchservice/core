package com.serch.server.models.subscription;

import com.serch.server.bases.BaseDateTime;
import com.serch.server.enums.subscription.SubPlanType;
import com.serch.server.generators.subscription.PlanChildID;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

/**
 * The PlanChild class represents a child plan associated with a parent plan in the company schema.
 * It stores information such as the child plan type, name, amount, discount, tag, and whether it's a business plan.
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
 *     <li>Many-to-one with {@link PlanParent} as the parent.</li>
 * </ul>
 */
@Getter
@Setter
@Entity
@Table(schema = "company", name = "plan_children")
public class PlanChild extends BaseDateTime {
    @Id
    @Column(name = "id", nullable = false, columnDefinition = "TEXT")
    @GenericGenerator(name = "plan_child_id_gen", type = PlanChildID.class)
    @GeneratedValue(generator = "plan_child_id_gen")
    private String id;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private SubPlanType type;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String amount;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String discount;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String tag;

    @Column(name = "is_business", nullable = false)
    private Boolean isBusiness = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "parent_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "plan_parent_id_fkey")
    )
    private PlanParent parent;
}
