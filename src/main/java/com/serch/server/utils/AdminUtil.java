package com.serch.server.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
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
        return ZonedDateTime.of(LocalDateTime.of(Objects.requireNonNullElseGet(
                year,
                () -> LocalDate.now().getYear()), 1, 1, 0, 0
        ), TimeUtil.defaultZone());
    }

    /**
     * Formats a numeric value into a human-readable string with appropriate
     * suffixes for large numbers (thousand, million, billion) or as-is for smaller values.
     *
     * - Numbers in the billions are suffixed with "billion" and rounded to one decimal place.
     * - Numbers in the millions are suffixed with "million" and rounded to one decimal place.
     * - Numbers in the thousands include commas for readability.
     * - Numbers below a thousand are returned as-is.
     *
     * Examples:
     * - Input: 1500 -> Output: "1,500"
     * - Input: 1_200_000 -> Output: "1.2 million"
     * - Input: 3_000_000_000 -> Output: "3.0 billion"
     *
     * @param count The numeric value to format.
     * @return A string representation of the formatted value.
     */
    public static String formatCount(Long count) {
        if (count >= 1_000_000_000) {
            return String.format("%.1f billion", count / 1_000_000_000.0);
        } else if (count >= 1_000_000) {
            return String.format("%.1f million", count / 1_000_000.0);
        } else if (count >= 1_000) {
            return String.format("%,d", count);
        } else {
            return count.toString();
        }
    }
}
