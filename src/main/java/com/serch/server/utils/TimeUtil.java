package com.serch.server.utils;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * The TimeUtil class provides utility methods for working with dates and times.
 */
public class TimeUtil {
    /**
     * Formats the time difference in minutes, hours, or days before or after the current time.
     * @param diff The time difference in minutes.
     * @return The formatted time difference string.
     */
    public static String formatTimeDifference(long diff) {
        long absDiff = Math.abs(diff);
        if (diff < 0) {
            if (absDiff < 60) {
                return absDiff + " minutes before";
            } else {
                long hours = absDiff / 60;
                long minutes = absDiff % 60;
                if (minutes == 0) {
                    return hours + " hour(s) before";
                } else {
                    return hours + " hour(s) and " + minutes + " minutes before";
                }
            }
        } else {
            if (absDiff < 60) {
                return absDiff + " minutes after";
            } else {
                long hours = absDiff / 60;
                long minutes = absDiff % 60;
                if (minutes == 0) {
                    return hours + " hour(s) after";
                } else {
                    return hours + " hour(s) and " + minutes + " minutes after";
                }
            }
        }
    }

    /**
     * Formats the last seen time based on the provided last seen date and time.
     * @param lastSignedIn The date and time when the user was last seen.
     * @return The formatted last signed in time.
     */
    public static String formatLastSignedIn(LocalDateTime lastSignedIn, boolean needText) {
        if(lastSignedIn == null) {
            return "Never";
        } else {
            Duration duration = Duration.between(lastSignedIn, LocalDateTime.now());

            if (duration.toDays() > 0) {
                if (duration.toDays() == 1) {
                    return needText
                            ? "Last Signed In: 1 day ago"
                            : "1 day ago";
                } else {
                    return needText
                            ? "Last Signed In: " + duration.toDays() + " days ago"
                            : duration.toDays() + " days ago";
                }
            } else if (duration.toHours() > 0) {
                if (duration.toHours() == 1) {
                    return needText
                            ? "Last Signed In: 1 hour ago"
                            : "1 hour ago";
                } else {
                    return needText
                            ? "Last Signed In: " + duration.toHours() + " hours ago"
                            : duration.toHours() + " hours ago";
                }
            } else if (duration.toMinutes() > 0) {
                if (duration.toMinutes() == 1) {
                    return needText
                            ? "Last Signed In: 1 minute ago"
                            : "1 minute ago";
                } else {
                    return needText
                            ? "Last Signed In: " + duration.toMinutes() + " minutes ago"
                            : duration.toMinutes() + " minutes ago";
                }
            } else {
                return "Online";
            }
        }
    }

    /**
     * Formats the future time based on the provided date and time.
     * @param dateTime The future date and time.
     * @return The formatted future time.
     */
    public static String formatFutureTime(LocalDateTime dateTime) {
        Duration duration = Duration.between(LocalDateTime.now(), dateTime);
        long seconds = duration.toSeconds() % 60;
        long minutes = duration.toMinutes() % 60;

        StringBuilder result = new StringBuilder();
        if (minutes > 0) {
            result.append(minutes).append(" ")
                    .append(minutes == 1 ? "minute" : "minutes")
                    .append(" ")
                    .append(seconds).append(" ")
                    .append(seconds == 1 ? "second" : "seconds");
        } else {
            result.append(seconds).append(" ")
                    .append(seconds == 1 ? "second" : "seconds");
        }
        return result.toString();
    }

    /**
     * Formats the time based on the provided date and time.
     * @param dateTime The date and time to format.
     * @return The formatted time.
     */
    public static String formatTime(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("h:mma"));
    }

    /**
     * Formats the day based on the provided date and time.
     * @param dateTime The date and time to format.
     * @return The formatted day.
     */
    public static String formatDay(LocalDateTime dateTime) {
        if(dateTime != null) {
            LocalDateTime currentDateTime = LocalDateTime.now();

            if (dateTime.toLocalDate().equals(currentDateTime.toLocalDate())) {
                return "Today, %s".formatted(formatTime(dateTime));
            } else if (dateTime.toLocalDate().equals(currentDateTime.minusDays(1).toLocalDate())) {
                return "Yesterday, %s".formatted(formatTime(dateTime));
            } else {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy");
                return dateTime.format(formatter);
            }
        } else {
            return "";
        }
    }

    public static String formatChatLabel(LocalDateTime date) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime today = now.truncatedTo(ChronoUnit.DAYS);
        LocalDateTime yesterday = today.minusDays(1);
        LocalDateTime lastWeek = today.minusDays(7);

        if (date.truncatedTo(ChronoUnit.DAYS).isEqual(today)) {
            return "Today";
        } else if (date.truncatedTo(ChronoUnit.DAYS).isEqual(yesterday)) {
            return "Yesterday";
        } else if (date.isAfter(lastWeek)) {
            List<String> weekdayNames = Arrays.asList(
                    "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
            );
            DayOfWeek dayOfWeek = date.getDayOfWeek();
            return weekdayNames.get(dayOfWeek.getValue() - 1);
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE MMMM d, yyyy");
            return date.format(formatter);
        }
    }

    /**
     * Checks if the OTP (One-Time Password) has expired based on the provided time and expiration time.
     * @param time The time the OTP was generated.
     * @param expirationTime The expiration time for the OTP in minutes.
     * @return True if the OTP has expired; otherwise, false.
     */
    public static boolean isOtpExpired(LocalDateTime time, int expirationTime) {
        if(time != null) {
            LocalDateTime expirationTimePoint = time.plusMinutes(expirationTime);
            return LocalDateTime.now().isAfter(expirationTimePoint);
        } else {
            return true;
        }
    }

    public static String log() {
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                "d MMMM, yyyy (EEEE) - h:mma",
                Locale.ENGLISH
        );
        return dateTime.format(formatter);
    }
}