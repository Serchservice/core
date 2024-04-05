package com.serch.server.services.media.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MediaUtil {
    public static String formatRegionAndDate(LocalDateTime date, String region) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, d'\"' MMMM");
        String dateTime = date.format(formatter);
        return dateTime + " | " + region;
    }
}
