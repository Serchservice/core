package com.serch.server.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.exceptions.SerchException;
import lombok.SneakyThrows;
import org.apache.commons.codec.binary.Base64;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HelperUtil {
    static String code(String firstName, String lastName, SerchCategory category) {
        if(category == SerchCategory.BUSINESS) {
            return "%sBusiness%s".formatted(firstName, UUID.randomUUID().toString().substring(0, 4)
                    .replaceAll("-", "").replaceAll("_", "")
            );
        } else {
            return (firstName.substring(0, 3) + lastName.substring(0, 3) +
                    UUID.randomUUID().toString().substring(0, 4))
                    .replaceAll("-", "")
                    .replaceAll("_", "");
        }
    };

    public static String generateReferralLink(String firstName, String lastName, SerchCategory category) {
        if(category == SerchCategory.USER) {
            return "https://serchservice.com/join_the_serch_user_app?ref=%s".formatted(
                    code(firstName, lastName, category)
            );
        } else if(category == SerchCategory.BUSINESS) {
            return "https://serchservice.com/join_the_serch_business_app?ref=%s".formatted(
                    code(firstName, lastName, category)
            );
        } else {
            return "https://serchservice.com/join_the_serch_provider_app?ref=%s".formatted(
                    code(firstName, lastName, category)
            );
        }
    }

    public static String extractReferralCode(String referralLink) {
        Pattern pattern = Pattern.compile("ref=([a-zA-Z0-9_]+)");
        Matcher matcher = pattern.matcher(referralLink);

        if (matcher.find()) {
            return matcher.group(1);
        } else {
            throw new SerchException("Cannot find referral code from referral link");
        }
    }

    public static String generateSharingLink(String firstName) {
        String code = (firstName + UUID.randomUUID().toString().substring(0, 10))
                .toLowerCase()
                .replaceAll("-", "")
                .replaceAll("_", "");
        return "https://serchservice.com/request_serch_services?shared_by=%s".formatted(code);
    }

    public static boolean validatePassword(String password) {
        Pattern pattern = Pattern.compile(
                "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$"
        );
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

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
