package com.serch.server.enums.subscription;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * The PlanStatus enum represents different statuses of a plan in the application.
 * Each enum constant corresponds to a specific plan status and provides a descriptive type.
 * <p></p>
 * The plan statuses are:
 * <ul>
 *     <li>{@link PlanStatus#ACTIVE} - Represents an active plan</li>
 *     <li>{@link PlanStatus#USED} - Represents a used plan</li>
 *     <li>{@link PlanStatus#NOT_USED} - Represents a not used plan</li>
 *     <li>{@link PlanStatus#SUSPENDED} - Represents a plan that is unsubscribed</li>
 *     <li>{@link PlanStatus#EXPIRED} - Represents an expired plan</li>
 * </ul>
 */
@Getter
@AllArgsConstructor
public enum PlanStatus {
    ACTIVE("Active"),
    USED("Used"),
    NOT_USED("Not Used"),
    SUSPENDED("Unsubscribed"),
    EXPIRED("Expired");

    private final String type;
}
