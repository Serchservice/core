package com.serch.server.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.SneakyThrows;
import org.apache.commons.codec.binary.Base64;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
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
        return "https://serchservice.com/request_serch_services?shared_by=%s".formatted(code);
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

    /**
     * Generates a Base64-encoded QR code image from the given content, width, and height.
     * @param content The content to encode in the QR code.
     * @param width The width of the QR code image.
     * @param height The height of the QR code image.
     * @return The Base64-encoded string representing the QR code image.
     */
    @SneakyThrows
    public static String generateQrCode(String content, Integer width, Integer height) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        BitMatrix bitMatrix = qrCodeWriter.encode(
                content,
                BarcodeFormat.QR_CODE,
                width != null ? width : 100,
                height != null ? height : 100
        );
        BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "PNG", outputStream);
        outputStream.flush();
        byte[] imageBytes = outputStream.toByteArray();
        return Base64.encodeBase64String(imageBytes);
    }

    /**
     * Formats a file size in bytes to a human-readable format (e.g., KB, MB, GB).
     * @param fileSizeInBytes The size of the file in bytes.
     * @return The formatted file size string.
     */
    public static String formatFileSize(long fileSizeInBytes) {
        if (fileSizeInBytes < 1024) {
            return fileSizeInBytes + " B";
        } else if (fileSizeInBytes < 1024 * 1024) {
            return String.format("%.2f KB", fileSizeInBytes / 1024.0);
        } else if (fileSizeInBytes < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", fileSizeInBytes / (1024.0 * 1024));
        } else {
            return String.format("%.2f GB", fileSizeInBytes / (1024.0 * 1024 * 1024));
        }
    }
}
