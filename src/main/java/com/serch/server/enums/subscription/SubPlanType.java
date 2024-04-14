package com.serch.server.enums.subscription;

import lombok.Getter;

/**
 * The SubPlanType enum represents different types of sub-plans available for a plan in the application.
 * Each enum constant corresponds to a specific sub-plan type and provides a descriptive type.
 * <p></p>
 * The sub-plan types are:
 * <ul>
 *     <li>{@link SubPlanType#DAILY} - Represents a daily sub-plan</li>
 *     <li>{@link SubPlanType#WEEKLY} - Represents a weekly sub-plan</li>
 *     <li>{@link SubPlanType#MONTHLY} - Represents a monthly sub-plan</li>
 *     <li>{@link SubPlanType#QUARTERLY} - Represents a sub-plan every three months</li>
 * </ul>
 */
@Getter
public enum SubPlanType {
    DAILY("Daily"),
    WEEKLY("Weekly"),
    MONTHLY("Monthly"),
    QUARTERLY("Every Three Months");

    private final String type;
    SubPlanType(String type) {
        this.type = type;
    }
}
