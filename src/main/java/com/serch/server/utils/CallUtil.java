package com.serch.server.utils;

import java.time.Duration;
import java.time.LocalDateTime;

public class CallUtil {
    public static int getHours(int duration) {
        return duration / 3600;
    }

    public static String formatCallTimeFromDate(LocalDateTime timestamp) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(timestamp, now);

        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static String formatCallTimeFromInt(int duration) {
        int hours = duration / 3600;
        int remainingSeconds = duration % 3600;
        int minutes = remainingSeconds / 60;
        int seconds = remainingSeconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
