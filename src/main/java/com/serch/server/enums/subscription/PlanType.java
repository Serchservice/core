package com.serch.server.enums.subscription;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.Set;

/**
 * The PlanType enum represents different types of plans in the application.
 * Each enum constant corresponds to a specific plan type and provides a descriptive type.
 * <p></p>
 * The plan types are:
 * <ul>
 *     <li>{@link PlanType#FREE} - Represents a free plan</li>
 *     <li>{@link PlanType#PAYU} - Represents a pay-as-you-use plan</li>
 *     <li>{@link PlanType#ALL_DAY} - Represents an all-day plan with daily, weekly, monthly, and quarterly options</li>
 *     <li>{@link PlanType#PREMIUM} - Represents a premium plan with daily, weekly, monthly, and quarterly options</li>
 * </ul>
 * <p></p>
 * @see SubPlanType
 */
@Getter
@RequiredArgsConstructor
public enum PlanType {
    FREE(Collections.emptySet(), "Free"),
    PAYU(Collections.emptySet(), "Pay-As-You-Use"),
    ALL_DAY(Set.of(
            SubPlanType.DAILY,
            SubPlanType.WEEKLY,
            SubPlanType.MONTHLY,
            SubPlanType.QUARTERLY
    ), "AllDay"),
    PREMIUM(Set.of(
            SubPlanType.DAILY,
            SubPlanType.WEEKLY,
            SubPlanType.MONTHLY,
            SubPlanType.QUARTERLY
    ), "Premium");

    private final Set<SubPlanType> plans;
    private final String type;
}
