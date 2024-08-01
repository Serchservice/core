package com.serch.server.utils;

import java.time.Year;
import java.util.List;
import java.util.stream.IntStream;

public class AdminUtil {
    public static Integer currentYear() {
        return Year.now().getValue();
    }

    public static List<Integer> years() {
        return IntStream.rangeClosed(2024, currentYear()).boxed().toList();
    }
}
