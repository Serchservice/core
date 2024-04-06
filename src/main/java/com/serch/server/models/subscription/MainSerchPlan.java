package com.serch.server.models.subscription;

import com.serch.server.bases.BaseModel;
import com.serch.server.enums.subscription.PlanType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(schema = "company", name = "plan_parents")
public class MainSerchPlan extends BaseModel {
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

    @Column(nullable = false, columnDefinition = "TEXT")
    private String amount;

    @Column(name = "with_business", nullable = false)
    private Boolean withBusiness = false;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SerchPlanBenefit> benefits;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SubSerchPlan> subPlans;
}
