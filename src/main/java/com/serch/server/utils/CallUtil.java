package com.serch.server.utils;

import com.serch.server.domains.conversation.responses.CallPeriodResponse;

import java.time.LocalDate;
import java.time.ZonedDateTime;

/**
 * The CallUtil class provides utility methods for handling call-related operations.
 * <p></p>
 * Utility Methods:
 * <ul>
 *     <li>{@link #isSession(int, int)}</li>
 * </ul>
 */
public class CallUtil {
    /**
     * Calculates the number of hours from the given duration in seconds.
     * @param duration The duration of the call in seconds.
     * @return The number of hours in the duration.
     */
    public static boolean isSession(int duration, int session) {
        // Check if the remainder when divided by 60 is equal to 30
        return duration % 60 == session || duration % 60 == (session + 1) || duration % 60 == (session + 2);
    }

    public static CallPeriodResponse getPeriod(String timezone) {
        ZonedDateTime startOfDay = ZonedDateTime.of(LocalDate.now().atStartOfDay(), TimeUtil.zoneId(timezone));

        CallPeriodResponse response = new CallPeriodResponse();
        response.setStart(startOfDay);
        response.setEnd(startOfDay.plusDays(1));

        return response;
    }
}
