package com.serch.server.models.subscription;

import com.serch.server.bases.BaseModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * The PlanBenefit class represents a benefit associated with a plan in the company schema.
 * It stores information such as the benefit description and the parent plan it belongs to.
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
@Table(schema = "company", name = "plan_benefits")
public class PlanBenefit extends BaseModel {
    @Column(nullable = false, columnDefinition = "TEXT")
    private String benefit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "parent_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "plan_parent_benefit_id_fkey")
    )
    private PlanParent parent;
}
