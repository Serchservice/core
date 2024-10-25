package com.serch.server.core.totp;

import lombok.SneakyThrows;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;


/**
 * This is an example implementation of the OATH
 * TOTP algorithm.
 * Visit www.openauthentication.org for more information.
 *
 * @author Johan Rydell, PortWise, Inc.
 */

public class TOTP {
    private TOTP() {}

    /**
     * This method uses the JCE to provide the crypto algorithm.
     * HMAC computes a Hashed Message Authentication Code with the
     * crypto hash algorithm as a parameter.
     *
     * @param crypto: the crypto algorithm (HmacSHA1, HmacSHA256,
     *                             HmacSHA512)
     * @param keyBytes: the bytes to use for the HMAC key
     * @param text: the message or text to be authenticated
     */
    @SneakyThrows
    private static byte[] hmac_sha(String crypto, byte[] keyBytes, byte[] text){
        Mac hmac = Mac.getInstance(crypto);
        hmac.init(new SecretKeySpec(keyBytes, "RAW"));
        return hmac.doFinal(text);
    }


    /**
     * This method converts a HEX string to Byte[]
     *
     * @param hex: the HEX string
     *
     * @return a byte array
     */
    private static byte[] hexStr2Bytes(String hex){
        // Adding one byte to get the right conversion
        // Values starting with "0" can be converted
        byte[] bArray = new BigInteger("10" + hex,16).toByteArray();

        // Copy all the REAL bytes, not the "first"
        byte[] ret = new byte[bArray.length - 1];
        for (int i = 0; i < ret.length; i++)
            ret[i] = bArray[i+1];
        return ret;
    }

    // 0 1  2   3    4     5      6       7        8
    private static final int[] DIGITS_POWER = {1,10,100,1000,10000,100000,1000000,10000000,100000000 };


    /**
     * This method generates a TOTP value for the given
     * set of parameters.
     *
     * @param key: the shared secret, HEX encoded
     * @param time: a value that reflects a time
     * @param returnDigits: number of digits to return
     *
     * @return a numeric String in base 10 that includes digits
     */

    public static String generateTOTP(String key, String time, String returnDigits){
        return generateTOTP(key, time, returnDigits, "HmacSHA1");
    }


//    /**
//     * This method generates a TOTP value for the given
//     * set of parameters.
//     *
//     * @param key: the shared secret, HEX encoded
//     * @param time: a value that reflects a time
//     * @param returnDigits: number of digits to return
//     *
//     * @return a numeric String in base 10 that includes digits
//     */
//
//    public static String generateTOTP256(String key, String time, String returnDigits){
//        return generateTOTP(key, time, returnDigits, "HmacSHA256");
//    }
//
//
//    /**
//     * This method generates a TOTP value for the given
//     * set of parameters.
//     *
//     * @param key: the shared secret, HEX encoded
//     * @param time: a value that reflects a time
//     * @param returnDigits: number of digits to return
//     *
//     * @return a numeric String in base 10 that includes digits
//     */
//    public static String generateTOTP512(String key, String time, String returnDigits){
//        return generateTOTP(key, time, returnDigits, "HmacSHA512");
//    }

    /**
     * This method generates a TOTP value for the given
     * set of parameters.
     *
     * @param key: the shared secret, HEX encoded
     * @param time: a value that reflects a time
     * @param returnDigits: number of digits to return
     * @param crypto: the crypto function to use
     *
     * @return a numeric String in base 10 that includes digits
     */

    public static String generateTOTP(String key, String time, String returnDigits, String crypto){
        int codeDigits = Integer.decode(returnDigits);
        StringBuilder result;

        // Using the counter
        // First 8 bytes are for the movingFactor
        // Compliant with base RFC 4226 (HOTP)
        StringBuilder timeBuilder = new StringBuilder(time);
        while (timeBuilder.length() < 16 )
            timeBuilder.insert(0, "0");
        time = timeBuilder.toString();

        // Get the HEX in a Byte[]
        byte[] hash = hmac_sha(crypto, hexStr2Bytes(key), hexStr2Bytes(time));

        // put selected bytes into result int
        int offset = hash[hash.length - 1] & 0xf;

        int binary = ((hash[offset] & 0x7f) << 24) | ((hash[offset + 1] & 0xff) << 16) | ((hash[offset + 2] & 0xff) << 8) | (hash[offset + 3] & 0xff);

        int otp = binary % DIGITS_POWER[codeDigits];

        result = new StringBuilder(Integer.toString(otp));
        while (result.length() < codeDigits) {
            result.insert(0, "0");
        }
        return result.toString();
    }
}