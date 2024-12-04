package com.serch.server.admin.services.scopes.account.responses.user;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AccountUserScopeActivityResponse {
    private String feature;
    private List<MonthlyActivity> activities = new ArrayList<>();

    /**
     * Represents a single month's activity data.
     */
    @Data
    public static class MonthlyActivity {
        private String month;
        private List<DailyActivity> days = new ArrayList<>();
    }

    /**
     * Represents a single day's activity value.
     */
    @Data
    public static class DailyActivity {
        private Integer day;
        private Double value;
    }
}