package com.serch.server.models.subscription;

import com.serch.server.bases.BaseModel;
import com.serch.server.enums.subscription.SubPlanType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(schema = "company", name = "plan_children")
public class SubSerchPlan extends BaseModel {
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

    @Column(name = "plan_code", columnDefinition = "TEXT", nullable = false)
    private String planCode;

    @Column(name = "is_business", nullable = false)
    private Boolean isBusiness = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "parent_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "active_serch_id_fkey")
    )
    private MainSerchPlan parent;
}
