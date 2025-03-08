package com.serch.server.domains.nearby.services.addon.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.enums.nearby.GoAddonPlanInterval;
import com.serch.server.enums.nearby.GoUserAddonStatus;
import lombok.Data;

@Data
public class GoUserAddonResponse {
    private Long id;
    private String name;
    private String description;
    private GoAddonPlanInterval interval;
    private GoUserAddonStatus status;
    private String amount;
    private Boolean recurring;
    private Constraint constraint;
    private Timeline timeline;
    private Switch switching;
    private Card card;
    private GoAddonResponse addon;

    @Data
    public static class Constraint {
        @JsonProperty("can_renew")
        private Boolean canRenew = false;

        @JsonProperty("can_cancel")
        private Boolean canCancel = false;

        @JsonProperty("can_switch")
        private Boolean canSwitch = false;

        @JsonProperty("can_activate")
        private Boolean canActivate = false;
    }

    @Data
    public static class Timeline {
        @JsonProperty("subscribed_at")
        private String subscribedAt;

        @JsonProperty("next_billing_date")
        private String nextBillingDate;
    }

    @Data
    public static class Switch {
        private String id;
        private String name;
        private String description;
        private String amount;
        private GoAddonPlanInterval interval;
        private String currency;

        @JsonProperty("starts_when")
        private String startsWhen;

        @JsonProperty("can_cancel")
        private Boolean canCancel = false;
    }

    @Data
    public static class Card {
        @JsonProperty("card_type")
        private String cardType;

        @JsonProperty("exp_month")
        private String expMonth;

        @JsonProperty("exp_year")
        private String expYear;

        private String last4;
        private String bin;
        private String bank;
        private String channel;

        @JsonProperty("country_code")
        private String countryCode;

        @JsonProperty("account_name")
        private String accountName;
    }
}