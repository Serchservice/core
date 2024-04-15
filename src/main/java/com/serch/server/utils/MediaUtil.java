package com.serch.server.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for media-related operations.
 * <p></p>
 * Methods:
 * <ol>
 *     <li>{@link MediaUtil#formatRegionAndDate(LocalDateTime, String)}</li>
 * </ol>
 */
public class MediaUtil {

    /**
     * Formats the provided date and region into a specific string format.
     *
     * @param date The date and time to be formatted.
     * @param region The region associated with the media content.
     * @return A formatted string containing the date, day of the week, and region.
     *
     * @see DateTimeFormatter
     * @see LocalDateTime
     */
    public static String formatRegionAndDate(LocalDateTime date, String region) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, d'\"' MMMM");
        String dateTime = date.format(formatter);
        return dateTime + " | " + region;
    }
}