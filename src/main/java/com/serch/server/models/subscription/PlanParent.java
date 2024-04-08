package com.serch.server.models.subscription;

import com.serch.server.bases.BaseDateTime;
import com.serch.server.enums.subscription.PlanType;
import com.serch.server.generators.subscription.PlanParentID;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;

@Getter
@Setter
@Entity
@Table(schema = "company", name = "plan_parents")
public class PlanParent extends BaseDateTime {
    @Id
    @Column(name = "id", nullable = false, columnDefinition = "TEXT")
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