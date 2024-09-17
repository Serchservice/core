package com.serch.server.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class AdminUtil {

    /**
     * Get the current year
     *
     * @return {@link Integer} in year
     */
    public static Integer currentYear() {
        return Year.now().getValue();
    }

    /**
     * Fetch all the years Serch has been in existence from 2024
     *
     * @return List of years
     */
    public static List<Integer> years() {
        return IntStream.rangeClosed(2024, currentYear()).boxed().toList();
    }

    /**
     * Get random color
     *
     * @return {@link String} of color
     */
    public static String randomColor() {
        Random random = new Random();
        int color = random.nextInt(0xFFFFFF);
        return String.format("#%06X", color);
    }

    /**
     * Get the start of the year
     *
     * @param year The year to start with
     *
     * @return {@link java.time.ZonedDateTime} of year
     */
    public static ZonedDateTime getStartYear(Integer year) {
        if (year != null) {
            return ZonedDateTime.of(LocalDateTime.of(year, 1, 1, 0, 0), TimeUtil.defaultZone());
        } else {
            LocalDate now = LocalDate.now();
            return ZonedDateTime.of(LocalDateTime.of(now.getYear(), 1, 1, 0, 0), TimeUtil.defaultZone());
        }
    }
}
