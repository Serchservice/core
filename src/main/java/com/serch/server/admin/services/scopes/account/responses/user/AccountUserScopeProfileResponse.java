package com.serch.server.admin.services.scopes.account.responses.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.admin.services.responses.AccountScopeDetailResponse;
import com.serch.server.admin.services.responses.CommonProfileResponse;
import com.serch.server.enums.account.AccountStatus;
import com.serch.server.enums.account.Gender;
import com.serch.server.enums.account.ProviderStatus;
import com.serch.server.enums.auth.Role;
import com.serch.server.enums.company.IssueStatus;
import com.serch.server.enums.verified.ConsentType;
import com.serch.server.enums.verified.VerificationStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class AccountUserScopeProfileResponse extends AccountScopeDetailResponse {
    private String id;
    private Details details;
    private Active active;
    private Organization organization;
    private Additional additional;
    private Delete delete;
    private Device device;
    private Setting setting;
    private Verification verification;
    private List<Integer> years = new ArrayList<>();

    @JsonProperty("app_rating")
    private AppRating appRating;

    @JsonProperty("phone_information")
    private PhoneInformation phoneInformation;

    private List<Skill> skills = new ArrayList<>();

    @Data
    public static class Timeline {
        @JsonProperty("created_at")
        private ZonedDateTime createdAt;

        @JsonProperty("updated_at")
        private ZonedDateTime updatedAt;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class AppRating extends Timeline {
        private Long id;
        private Double rating = 0.0;
        private String comment;
        private String app;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class Active extends Timeline {
        private Long id;
        private ProviderStatus status;
        private String address;
        private Double latitude;
        private Double longitude;

        @JsonProperty("place_id")
        private String placeId;
    }

    @Data
    public static class Details {
        private String image;
        private String category;
        private String gender;
        private Double rating;
        private AccountStatus status;
        private Role role;

        @JsonProperty("messaging_token")
        private String messagingToken;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class Skill extends Timeline {
        private Long id;
        private String skill;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class Organization extends Timeline {
        private String name;
        private String logo;
        private String description;
        private String address;
        private String contact;
        private String category;
        private String image;
        private CommonProfileResponse admin;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class Setting extends Timeline {
        private Long id;
        private Gender gender;

        @JsonProperty("show_only_verified")
        private Boolean showOnlyVerified;

        @JsonProperty("show_only_certified")
        private Boolean showOnlyCertified;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class Additional extends Timeline {
        private Long id;
        private String city;
        private String state;
        private String country;

        @JsonProperty("street_address")
        private String streetAddress;

        @JsonProperty("landmark")
        private String landMark;

        @JsonProperty("surety_status")
        private String suretyStatus;

        @JsonProperty("surety_first_name")
        private String suretyFirstName;

        @JsonProperty("surety_last_name")
        private String suretyLastName;

        @JsonProperty("surety_email")
        private String suretyEmail;

        @JsonProperty("surety_phone_number")
        private String suretyPhone;

        @JsonProperty("surety_address")
        private String suretyAddress;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class PhoneInformation extends Timeline {
        private String country;
        private String number;

        @JsonProperty("country_code")
        private String countryCode;

        @JsonProperty("iso_code")
        private String isoCode;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class Delete extends Timeline {
        private String id;
        private IssueStatus status;
    }

    @Data
    public static class Device {
        private String state;
        private String country;
        private String name;
        private String device;
        private String host;

        @JsonProperty("ip_address")
        private String ipAddress;

        @JsonProperty("operating_system")
        private String operatingSystem;

        @JsonProperty("operating_system_version")
        private String operatingSystemVersion;

        @JsonProperty("local_host")
        private String localHost;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class Verification extends Timeline {
        private VerificationStatus status;

        @JsonProperty("use_of_data_consent")
        private ConsentType useOfDataConsent;

        @JsonProperty("trip_consent")
        private ConsentType tripConsent;

        @JsonProperty("manner_consent")
        private ConsentType mannerConsent;

        @JsonProperty("regulation_consent")
        private ConsentType regulationConsent;

        @JsonProperty("liability_consent")
        private ConsentType liabilityConsent;

        @JsonProperty("user_data_status")
        private VerificationStatus userStatus;
    }
}