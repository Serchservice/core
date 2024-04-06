package com.serch.server.models.subscription;

import com.serch.server.bases.BaseModel;
import com.serch.server.enums.subscription.PlanType;
import com.serch.server.enums.subscription.SubPlanType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(schema = "subscription", name = "requests")
public class Subscription extends BaseModel {
    @Column(name = "access_code", columnDefinition = "TEXT", nullable = false)
    private String accessCode;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String reference;

    private Integer size = 1;

    @Column(name = "email_address", columnDefinition = "TEXT", nullable = false)
    private String emailAddress;

    @Column(name = "serch_id")
    private UUID serchId;

    @Enumerated(value = EnumType.STRING)
    private PlanType plan;

    @Column(name = "sub_plan")
    @Enumerated(value = EnumType.STRING)
    private SubPlanType subPlan;
}
