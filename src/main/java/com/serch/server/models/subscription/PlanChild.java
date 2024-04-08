package com.serch.server.models.subscription;

import com.serch.server.bases.BaseDateTime;
import com.serch.server.enums.subscription.SubPlanType;
import com.serch.server.generators.PlanChildID;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

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
