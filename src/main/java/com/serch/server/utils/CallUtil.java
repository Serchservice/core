package com.serch.server.utils;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * The CallUtil class provides utility methods for handling call-related operations.
 * <p></p>
 * Utility Methods:
 * <ul>
 *     <li>{@link #getHours(int)}</li>
 *     <li>{@link #formatCallTimeFromDate(LocalDateTime)}</li>
 *     <li>{@link #formatCallTimeFromInt(int)}</li>
 * </ul>
 */
public class CallUtil {
    /**
     * Calculates the number of hours from the given duration in seconds.
     * @param duration The duration of the call in seconds.
     * @return The number of hours in the duration.
     */
    public static int getHours(int duration) {
        return duration / 3600;
    }

    /**
     * Formats the call time from a given LocalDateTime to hours, minutes, and seconds.
     * @param timestamp The timestamp representing the start time of the call.
     * @return A formatted string representing the call time.
     */
    public static String formatCallTimeFromDate(LocalDateTime timestamp) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(timestamp, now);

        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    /**
     * Formats the call time from an integer duration (in seconds) to hours, minutes, and seconds.
     * @param duration The duration of the call in seconds.
     * @return A formatted string representing the call time.
     */
    public static String formatCallTimeFromInt(int duration) {
        int hours = duration / 3600;
        int remainingSeconds = duration % 3600;
        int minutes = remainingSeconds / 60;
        int seconds = remainingSeconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
