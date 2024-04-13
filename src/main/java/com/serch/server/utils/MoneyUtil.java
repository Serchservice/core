package com.serch.server.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MoneyUtil {
    public static String formatToNaira(BigDecimal amount) {
        // Format the BigDecimal as a currency string
        String result = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-NG")).format(amount);
        // Remove any non-breaking space characters from the formatted string
        result = result.replace("\u00A0", "");
        return result.replace("\u00A0", "");
    }

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

    public static Integer percentageSpent(BigDecimal total, BigDecimal spent) {
        return spent.divide(total, 2, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100")).intValue();
    }

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
}
