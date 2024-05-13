package com.serch.server.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * The MoneyUtil class provides utility methods for formatting currency, generating options based on an amount,
 * calculating the percentage spent, and generating shared information about money distribution.
 */
public class MoneyUtil {
    /**
     * Formats the given amount to Nigerian Naira currency format.
     * @param amount The amount to format.
     * @return The formatted amount in Nigerian Naira currency.
     */
    public static String formatToNaira(BigDecimal amount) {
        // Format the BigDecimal as a currency string
        String result = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-NG")).format(amount);
        // Remove any non-breaking space characters from the formatted string
        result = result.replace("\u00A0", "");
        return result.replace("\u00A0", "");
    }

    public static BigDecimal parseFromNaira(String nairaAmount) {
        if(nairaAmount != null && !nairaAmount.isEmpty()) {
            // Remove the naira symbol and any commas from the string
            String cleanAmount = nairaAmount.replaceAll("[^\\d.]", "");
            // Parse the cleaned string as a BigDecimal
            return new BigDecimal(cleanAmount);
        } else {
            return BigDecimal.ZERO;
        }
    }

    /**
     * Generates a list of options based on the given amount with dynamic intervals.
     * @param amount The base amount to generate options from.
     * @return A list of formatted options.
     */
    public static List<String> generateOptionsFromAmount(BigDecimal amount) {
        List<String> options = new ArrayList<>();
        BigDecimal minAmount = amount
                .multiply(BigDecimal.valueOf(80))
                .divide(BigDecimal.valueOf(100), 0, RoundingMode.DOWN);
        BigDecimal maxAmount = amount
                .multiply(BigDecimal.valueOf(120))
                .divide(BigDecimal.valueOf(100), 0, RoundingMode.UP);
        BigDecimal dynamicInterval = amount.divide(BigDecimal.valueOf(24), 0, RoundingMode.DOWN);

        for (BigDecimal i = minAmount; i.compareTo(maxAmount) <= 0; i = i.add(dynamicInterval)) {
            options.add(formatToNaira(i));
        }
        return options;
    }

    /**
     * Calculates the percentage spent given the total and spent amounts.
     * @param total The total amount.
     * @param spent The amount spent.
     * @return The percentage spent.
     */
    public static Integer percentageSpent(BigDecimal total, BigDecimal spent) {
        return spent.divide(total, 2, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100")).intValue();
    }

    /**
     * Generates shared information about the money distribution based on provided parameters.
     * @param provider The name of the service provider.
     * @param user The name of the user.
     * @param id The ID of the shared link.
     * @param total The total amount.
     * @param userAmount The amount received by the user.
     * @param providerAmount The amount received by the provider.
     * @return The shared information about money distribution.
     */
    public static String getSharedInfo(
            String provider, String user, String id, BigDecimal total,
            BigDecimal userAmount, BigDecimal providerAmount
    ) {
        int userPercent = percentageSpent(total, userAmount);
        int providerPercent = percentageSpent(total, providerAmount);
        int serchPercent = 100 - (userPercent + providerPercent);

        BigDecimal serch = total.subtract(userAmount.add(providerAmount));

        return "Money from the shared link " + id + " was spent as follows:\n\n" +
                user + " (Serch User) received " + formatToNaira(userAmount) + " - " + userPercent + "%\n" +
                provider + " (Serch Provider) received " + formatToNaira(providerAmount) + " - " + providerPercent + "%\n" +
                "Serchservice Inc. received " + formatToNaira(serch) + " - " + serchPercent + "% for taxes.";
    }

    public static BigDecimal getDecimal(int percentage, BigDecimal amount) {
        BigDecimal divided = amount.divide(BigDecimal.valueOf(percentage), RoundingMode.HALF_UP);
        return divided.multiply(BigDecimal.valueOf(percentage));
    }
}
