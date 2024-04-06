package com.serch.server.enums.subscription;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.Set;

@Getter
@RequiredArgsConstructor
public enum PlanType {
    FREE(Collections.emptySet(), "Serch Free"),
    PAYU(Collections.emptySet(), "Serch Pay-As-You-Use"),
    ALL_DAY(Set.of(
            SubPlanType.DAILY,
            SubPlanType.WEEKLY,
            SubPlanType.MONTHLY,
            SubPlanType.QUARTERLY
    ), "Serch AllDay"),
    PREMIUM(Set.of(
            SubPlanType.DAILY,
            SubPlanType.WEEKLY,
            SubPlanType.MONTHLY,
            SubPlanType.QUARTERLY
    ), "Serch Premium");

    private final Set<SubPlanType> plans;
    private final String type;
}
