package com.serch.server.utils;

import com.serch.server.services.conversation.responses.CallPeriodResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * The CallUtil class provides utility methods for handling call-related operations.
 * <p></p>
 * Utility Methods:
 * <ul>
 *     <li>{@link #isSession(int)}</li>
 * </ul>
 */
public class CallUtil {
    /**
     * Calculates the number of hours from the given duration in seconds.
     * @param duration The duration of the call in seconds.
     * @return The number of hours in the duration.
     */
    public static boolean isSession(int duration) {
        // Calculate the total minutes from the duration
        int totalMinutes = duration / 60;

        // Check if the remainder when divided by 60 is equal to 30
        return totalMinutes % 60 == 30;
    }

    public static CallPeriodResponse getPeriod() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();

        CallPeriodResponse response = new CallPeriodResponse();
        response.setStart(startOfDay);
        response.setEnd(startOfDay.plusDays(1));

        return response;
    }
}
