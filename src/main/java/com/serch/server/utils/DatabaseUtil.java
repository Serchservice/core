package com.serch.server.utils;

import java.util.Base64;

public class DatabaseUtil {
    public static String encodeData(String data) {
        return Base64.getEncoder().encodeToString(data.getBytes());
    }

    public static String decodeData(String encodedData) {
        byte[] decodedBytes = Base64.getDecoder().decode(encodedData);
        return new String(decodedBytes);
    }
}
