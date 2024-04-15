package com.serch.server.models.subscription;

import com.serch.server.bases.BaseDateTime;
import com.serch.server.enums.subscription.PlanType;
import com.serch.server.generators.subscription.PlanParentID;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;

/**
 * The PlanParent class represents a parent plan in the company schema. It stores information such as
 * the plan type, description, image, color, duration, and associated benefits and children plans.
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
 *     <li>One-to-many with {@link PlanBenefit} as the benefits.</li>
 *     <li>One-to-many with {@link PlanChild} as the children.</li>
 * </ul>
 */
@Getter
@Setter
@Entity
@Table(schema = "company", name = "plan_parents")
public class PlanParent extends BaseDateTime {
    @Id
    @Column(name = "id", nullable = false, columnDefinition = "TEXT", unique = true)
    @GenericGenerator(name = "plan_id_gen", type = PlanParentID.class)
    @GeneratedValue(generator = "plan_id_gen")
    private String id;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private PlanType type;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String image;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String color;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String duration;

    @Column(columnDefinition = "TEXT")
    private String amount;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PlanBenefit> benefits;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PlanChild> children;
}