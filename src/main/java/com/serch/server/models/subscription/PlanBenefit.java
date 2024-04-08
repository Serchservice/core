package com.serch.server.models.subscription;

import com.serch.server.bases.BaseModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
