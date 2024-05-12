package com.serch.server.utils;

import com.serch.server.enums.subscription.PlanType;
import com.serch.server.enums.subscription.SubPlanType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * The TimeUtil class provides utility methods for working with dates and times.
 */
public class TimeUtil {
    /**
     * Calculates the time for a subscription plan based on the given duration.
     * @param dateTime The base date and time.
     * @param duration The duration of the subscription plan.
     * @return The calculated date and time.
     */
    private static LocalDateTime calculateSubPlanTime(LocalDateTime dateTime, SubPlanType duration) {
        return switch (duration) {
            case DAILY -> dateTime.plusDays(1);
            case WEEKLY -> dateTime.plusWeeks(1);
            case MONTHLY -> dateTime.plusMonths(1);
            case QUARTERLY -> dateTime.plusMonths(3);
        };
    }

    /**
     * Calculates the time for a plan based on the given plan type and duration.
     * @param dateTime The base date and time.
     * @param plan The type of plan.
     * @param duration The duration of the plan.
     * @return The calculated date and time.
     */
    private static LocalDateTime calculatePlanTime(
            LocalDateTime dateTime, PlanType plan, SubPlanType duration
    ) {
        return switch (plan) {
            case FREE -> dateTime.plusDays(21);
            case ALL_DAY, PREMIUM -> calculateSubPlanTime(dateTime, duration);
            case PAYU -> dateTime.plusDays(1);
        };
    }

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
     * Checks if the provided time is expired compared to the current time.
     * @param time The time to check for expiration.
     * @return True if the provided time is expired; otherwise, false.
     */
    public static boolean isExpired(LocalDateTime time) {
        return LocalDateTime.now().isAfter(time);
    }

    /**
     * Formats the remaining time of a subscription plan based on the provided creation time, plan type, and sub-plan type.
     * @param createdAt The date and time of plan creation.
     * @param plan The subscription plan type.
     * @param sub The sub-plan type.
     * @return The formatted remaining time of the subscription plan.
     */
    public static String formatPlanTime(LocalDateTime createdAt, PlanType plan, SubPlanType sub) {
        LocalDateTime endTime = calculatePlanTime(createdAt, plan, sub);
        if (isExpired(endTime)) {
            return "Subscription expired";
        }

        Duration duration = Duration.between(LocalDateTime.now(), endTime);
        long days = duration.toDays();
        long hours = duration.toHours() % 24;
        long minutes = duration.toMinutes() % 60;

        StringBuilder result = new StringBuilder();
        if (days > 0) {
            result.append("Remaining").append(" ")
                    .append(days).append(" ")
                    .append(days == 1 ? "day" : "days");
        } else if (hours > 0) {
            result.append("Remaining").append(" ")
                    .append(hours).append(" ")
                    .append(hours == 1 ? "hour" : "hours");
        } else if (minutes > 0) {
            result.append("Remaining").append(" ")
                    .append(minutes).append(" ")
                    .append(minutes == 1 ? "minute" : "minutes");
        } else {
            return "Expired";
        }

        return !result.isEmpty() ? result.toString() : "Just expired";
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
                return "Today";
            } else if (dateTime.toLocalDate().equals(currentDateTime.minusDays(1).toLocalDate())) {
                return "Yesterday";
            } else {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy");
                return dateTime.format(formatter);
            }
        } else {
            return "";
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

    public static String log(LocalDateTime date) {
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                "d MMMM, yyyy (EEEE) - h:mma",
                Locale.ENGLISH
        );
        return dateTime.format(formatter);
    }
}