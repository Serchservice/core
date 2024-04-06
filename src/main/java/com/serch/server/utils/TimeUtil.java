package com.serch.server.utils;

import com.serch.server.enums.subscription.PlanType;
import com.serch.server.enums.subscription.SubPlanType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class TimeUtil {
    private static LocalDateTime calculateSubPlanTime(LocalDateTime dateTime, SubPlanType duration) {
        return switch (duration) {
            case DAILY -> dateTime.plusDays(1);
            case WEEKLY -> dateTime.plusWeeks(1);
            case MONTHLY -> dateTime.plusMonths(1);
            case QUARTERLY -> dateTime.plusMonths(3);
        };
    }

    private static LocalDateTime calculatePlanTime(
            LocalDateTime dateTime, PlanType plan, SubPlanType duration
    ) {
        return switch (plan) {
            case FREE -> dateTime.plusDays(21);
            case ALL_DAY, PREMIUM -> calculateSubPlanTime(dateTime, duration);
            case PAYU -> dateTime.plusDays(1);
        };
    }

    public static boolean isExpired(LocalDateTime time) {
        return LocalDateTime.now().isAfter(time);
    }

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

    public static String formatLastSeen(LocalDateTime lastSeenDateTime) {
        Duration duration = Duration.between(lastSeenDateTime, LocalDateTime.now());

        if (duration.toDays() > 0) {
            if (duration.toDays() == 1) {
                return "Last Seen: 1 day ago";
            } else {
                return "Last Seen: " + duration.toDays() + " days ago";
            }
        } else if (duration.toHours() > 0) {
            if (duration.toHours() == 1) {
                return "Last Seen: 1 hour ago";
            } else {
                return "Last Seen: " + duration.toHours() + " hours ago";
            }
        } else if (duration.toMinutes() > 0) {
            if (duration.toMinutes() == 1) {
                return "Last Seen: 1 minute ago";
            } else {
                return "Last Seen: " + duration.toMinutes() + " minutes ago";
            }
        } else {
            return "Online";
        }
    }

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

    public static String formatTime(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("h:mma"));
    }

    public static String formatDay(LocalDateTime dateTime) {
        LocalDateTime currentDateTime = LocalDateTime.now();

        if (dateTime.toLocalDate().equals(currentDateTime.toLocalDate())) {
            return "Today";
        } else if (dateTime.toLocalDate().equals(currentDateTime.minusDays(1).toLocalDate())) {
            return "Yesterday";
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy");
            return dateTime.format(formatter);
        }
    }

    public static boolean isOtpExpired(LocalDateTime time, int expirationTime) {
        if(time != null) {
            long minutesSinceLastOtp = ChronoUnit.MINUTES.between(time, LocalDateTime.now());
            return minutesSinceLastOtp >= expirationTime;
        } else {
            return true;
        }
    }
}
