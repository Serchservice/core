package com.serch.server.utils;

import com.serch.server.services.supabase.requests.FileUploadRequest;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The HelperUtil class provides utility methods for various operations such as generating referral links,
 * extracting referral codes from links, generating sharing links, validating passwords, calculating distance
 * between two geographic points, generating QR codes, and formatting file sizes.
 */
public class HelperUtil {
    /**
     * Generates a sharing link based on the user's first name.
     * @param firstName The first name of the user.
     * @return The generated sharing link.
     */
    public static String generateSharingLink(String firstName) {
        String code = (firstName + UUID.randomUUID().toString().substring(0, 10))
                .toLowerCase()
                .replaceAll("-", "")
                .replaceAll("_", "");
        return "https://serchservice.com/service/request?shared_by=%s".formatted(code);
    }

    /**
     * Validates a password based on a regular expression pattern.
     * @param password The password to validate.
     * @return True if the password is valid, otherwise false.
     */
    public static boolean validatePassword(String password) {
        Pattern pattern = Pattern.compile(
                "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$"
        );
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    /**
     * Calculates the distance between two geographic points using the Haversine formula.
     * @param lat1 Latitude of the first point.
     * @param lon1 Longitude of the first point.
     * @param lat2 Latitude of the second point.
     * @param lon2 Longitude of the second point.
     * @return The distance between the two points in kilometers.
     */
    public static double getDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth radius in kilometers

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return Math.round(R * c * 100.0) / 100.0; // Distance in kilometers rounded to two decimal places
    }

    public static boolean isUploadEmpty(FileUploadRequest request) {
        return request == null || request.getPath().isEmpty() || request.getBytes() == null;
    }

    public static String generateReference(String prefix) {
        return "%s%s".formatted(prefix, UUID.randomUUID().toString().substring(0, 8));
    }
}
