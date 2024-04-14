package com.serch.server.enums.shared;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The UseStatus enum represents different statuses of usage in the application.
 * Each enum constant corresponds to a specific usage status and provides a descriptive type and count.
 * <p></p>
 * The usage statuses are:
 * <ul>
 *     <li>{@link UseStatus#USED} - Represents a used status with a count of 5</li>
 *     <li>{@link UseStatus#COUNT_1} - Represents a status with a count of 1</li>
 *     <li>{@link UseStatus#COUNT_2} - Represents a status with a count of 2</li>
 *     <li>{@link UseStatus#COUNT_3} - Represents a status with a count of 3</li>
 *     <li>{@link UseStatus#COUNT_4} - Represents a status with a count of 4</li>
 *     <li>{@link UseStatus#NOT_USED} - Represents a not used status with a count of 0</li>
 * </ul>
 */
@Getter
@RequiredArgsConstructor
public enum UseStatus {
    USED("Used", 5),
    COUNT_1("Count1", 1),
    COUNT_2("Count2", 2),
    COUNT_3("Count3", 3),
    COUNT_4("Count4", 4),
    NOT_USED("Not Used", 0);

    private final String type;
    private final Integer count;
}