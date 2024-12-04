package com.serch.server.admin.services.scopes.account.services.implementation;

import com.serch.server.admin.services.responses.ChartMetric;
import com.serch.server.admin.services.scopes.account.responses.AccountScopeAnalysisResponse;
import com.serch.server.admin.services.scopes.account.services.AccountScopeAnalysisService;
import com.serch.server.admin.services.scopes.account.services.AccountScopeService;
import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.account.AccountStatus;
import com.serch.server.enums.account.ProviderStatus;
import com.serch.server.enums.auth.Role;
import com.serch.server.exceptions.others.SerchException;
import com.serch.server.models.auth.User;
import com.serch.server.models.shared.Guest;
import com.serch.server.repositories.account.BusinessProfileRepository;
import com.serch.server.repositories.account.ProfileRepository;
import com.serch.server.repositories.auth.AccountStatusTrackerRepository;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.repositories.certificate.CertificateRepository;
import com.serch.server.repositories.shared.GuestRepository;
import com.serch.server.repositories.trip.ActiveRepository;
import com.serch.server.utils.AdminUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountScopeImplementation implements AccountScopeService {
    private final AccountScopeAnalysisService analysisService;
    private final GuestRepository guestRepository;
    private final UserRepository userRepository;
    private final BusinessProfileRepository businessProfileRepository;
    private final ProfileRepository profileRepository;
    private final AccountStatusTrackerRepository accountStatusTrackerRepository;
    private final ActiveRepository activeRepository;
    private final CertificateRepository certificateRepository;

    @Override
    public ApiResponse<AccountScopeAnalysisResponse> fetchAccountAnalysis(Integer year, Role role, Boolean forGuest) {
        validate(role, forGuest);

        ZonedDateTime start = AdminUtil.getStartYear(year);

        AccountScopeAnalysisResponse response = new AccountScopeAnalysisResponse();
        response.setYears(AdminUtil.years());

        List<ChartMetric> metrics = new ArrayList<>();
        if(forGuest != null && forGuest) {
            response.setContent("Total guest size");
            response.setTotal(guestRepository.count());

            for (int month = 1; month <= 12; month++) {
                ZonedDateTime startMonth = start.withMonth(month);

                ChartMetric metric = new ChartMetric();
                metric.setLabel(startMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH));
                metric.setValue(guestRepository.countByDateRange(startMonth, startMonth.plusMonths(1).minusSeconds(1)));
                metric.setColor(AdminUtil.randomColor());
                metrics.add(metric);
            }
        } else {
            response.setContent(String.format("Total %s size", role.getType().toLowerCase()));
            response.setTotal(userRepository.countByRole(role));

            for (int month = 1; month <= 12; month++) {
                ZonedDateTime startMonth = start.withMonth(month);

                ChartMetric metric = new ChartMetric();
                metric.setLabel(startMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH));
                metric.setValue(userRepository.countByRoleAndDateRange(role, startMonth, startMonth.plusMonths(1).minusSeconds(1)));
                metric.setColor(AdminUtil.randomColor());
                metrics.add(metric);
            }
        }
        response.setMetrics(metrics);

        return new ApiResponse<>(response);
    }

    void validate(Role role, Boolean forGuest) {
        if(role == null && (forGuest == null || !forGuest)) {
            throw new SerchException("You need to specify the account you're trying to fetch its analysis data");
        }
    }

    @Override
    public ApiResponse<AccountScopeAnalysisResponse> fetchGroupedAccountAnalysisByCountry(Integer year, Role role, Boolean forGuest) {
        return fetchGroupedAccountAnalysis(year, role, forGuest, "country", "size by country");
    }

    @Override
    public ApiResponse<AccountScopeAnalysisResponse> fetchGroupedAccountAnalysisByTimezone(Integer year, Role role, Boolean forGuest) {
        return fetchGroupedAccountAnalysis(year, role, forGuest, "timezone", "size by timezone");
    }

    @Override
    public ApiResponse<AccountScopeAnalysisResponse> fetchGroupedAccountAnalysisByState(Integer year, Role role, Boolean forGuest) {
        return fetchGroupedAccountAnalysis(year, role, forGuest, "state", "size by state");
    }

    @Override
    public ApiResponse<AccountScopeAnalysisResponse> fetchGroupedAccountAnalysisByGender(Integer year, Role role, Boolean forGuest) {
        return fetchGroupedAccountAnalysis(year, role, forGuest, "gender", "size by gender");
    }

    @Override
    public ApiResponse<AccountScopeAnalysisResponse> fetchGroupedAccountAnalysisByRating(Integer year, Role role, Boolean forGuest) {
        return fetchGroupedAccountAnalysis(year, role, forGuest, "rating", "size by rating");
    }

    @Override
    public ApiResponse<AccountScopeAnalysisResponse> fetchGroupedAccountAnalysisByAccountStatus(Integer year, Role role) {
        return fetchGroupedAccountAnalysis(year, role, false, "status", "size by account status");
    }

    @Override
    public ApiResponse<AccountScopeAnalysisResponse> fetchGroupedAccountAnalysisByTripStatus(Integer year, Role role) {
        return fetchGroupedAccountAnalysis(year, role, false, "trip", "size by trip status");
    }

    @Override
    public ApiResponse<AccountScopeAnalysisResponse> fetchGroupedAccountAnalysisByCertified(Integer year, Role role) {
        return fetchGroupedAccountAnalysis(year, role, false, "certification", "size by certification");
    }

    @Override
    public ApiResponse<AccountScopeAnalysisResponse> fetchGroupedAccountAnalysisByCategory(Integer year, Role role) {
        return fetchGroupedAccountAnalysis(year, role, false, "category", "size by category");
    }

    private ApiResponse<AccountScopeAnalysisResponse> fetchGroupedAccountAnalysis(Integer year, Role role, Boolean forGuest, String groupBy, String content) {
        validate(role, forGuest);

        ZonedDateTime start = AdminUtil.getStartYear(year);

        AccountScopeAnalysisResponse response = new AccountScopeAnalysisResponse();
        response.setYears(AdminUtil.years());
        response.setContent(String.format("%s %s", forGuest != null && forGuest ? "Guest" : role.getType(), content));

        List<ChartMetric> metrics = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            ZonedDateTime startMonth = start.withMonth(month);
            ZonedDateTime endMonth = startMonth.plusMonths(1).minusSeconds(1);

            ChartMetric monthMetric = new ChartMetric();
            monthMetric.setLabel(startMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH));

            if (forGuest != null && forGuest) {
                // Grouping for guests based on the selected grouping criterion
                buildGuestMetrics(groupBy, startMonth, endMonth, monthMetric);
                buildGuestInfoMetrics(groupBy, start, response);
            } else {
                // Grouping for users based on the selected grouping criterion
                buildUserMetrics(role, groupBy, startMonth, endMonth, monthMetric);
                buildUserInfoMetrics(role, groupBy, start, response);
            }

            metrics.add(monthMetric);
        }

        response.setMetrics(metrics);
        return new ApiResponse<>(response);
    }

    private void buildGuestMetrics(String groupBy, ZonedDateTime start, ZonedDateTime end, ChartMetric metric) {
        List<Guest> guests = guestRepository.findByCreatedAtBetween(start, end);

        metric.setMetrics(createMetricList(buildGuestMetrics(groupBy, guests), "Guest audience"));
    }

    private Map<String, Long> buildGuestMetrics(String groupBy, List<Guest> guests) {
        return switch (groupBy) {
            case "rating" -> groupGuestMetricsByRating(guests);
            case "gender" -> groupGuestMetricsByGender(guests);
            case "country" -> guests.stream().collect(Collectors.groupingBy(Guest::getCountry, Collectors.counting()));
            case "timezone" -> guests.stream().collect(Collectors.groupingBy(Guest::getTimezone, Collectors.counting()));
            default -> guests.stream().collect(Collectors.groupingBy(Guest::getState, Collectors.counting()));
        };
    }

    private void buildGuestInfoMetrics(String groupBy, ZonedDateTime start, AccountScopeAnalysisResponse response) {
        List<Guest> guests = guestRepository.findByCreatedAtBetween(start, start.plusYears(1));

        response.setInfoMetrics(createMetricList(buildGuestMetrics(groupBy, guests), "Guest audience"));
    }

    private Map<String, Long> groupGuestMetricsByRating(List<Guest> guests) {
        Map<String, Long> groups = getEmptyRating();
        groups.putAll(guests.stream().collect(Collectors.groupingBy(p -> String.format("%.1f", (double) Math.round(p.getRating())), Collectors.counting())));

        return groups;
    }

    private Map<String, Long> getEmptyRating() {
        Map<String, Long> groups = new LinkedHashMap<>();
        for (int rating = 0; rating <= 5; rating++) {
            groups.put(String.format("%.1f", (double) rating), 0L);
        }

        return groups;
    }

    private Map<String, Long> groupGuestMetricsByGender(List<Guest> guests) {
        Map<String, Long> groups = analysisService.getGender();
        groups.putAll(guests.stream().collect(Collectors.groupingBy(guest -> guest.getGender().getType(), Collectors.counting())));

        return groups;
    }

    private void buildUserMetrics(Role role, String groupBy, ZonedDateTime start, ZonedDateTime end, ChartMetric metric) {
        List<User> users = userRepository.findByRoleAndCreatedAtBetween(role, start, end);

        metric.setMetrics(createMetricList(buildUserMetrics(groupBy, users, role, start, end), String.format("%s audience", role.getType())));
    }

    private Map<String, Long> buildUserMetrics(String groupBy, List<User> users, Role role, ZonedDateTime start, ZonedDateTime end) {
        return switch (groupBy) {
            case "rating" -> groupUserMetricsByRating(role, start, end);
            case "category" -> groupUserMetricsByCategory(role, start, end);
            case "gender" -> groupUserMetricsByGender(role, start, end);
            case "status" -> groupUserMetricsByStatus(role, start, end);
            case "trip" -> groupUserMetricsByTrip(role, start, end);
            case "certificate" -> groupUserMetricsByCertificate(users);
            case "country" -> users.stream().collect(Collectors.groupingBy(User::getCountry, Collectors.counting()));
            case "state" -> users.stream().collect(Collectors.groupingBy(User::getState, Collectors.counting()));
            default -> users.stream().collect(Collectors.groupingBy(User::getTimezone, Collectors.counting()));
        };
    }

    private void buildUserInfoMetrics(Role role, String groupBy, ZonedDateTime start, AccountScopeAnalysisResponse response) {
        ZonedDateTime end = start.plusYears(1);
        List<User> users = userRepository.findByRoleAndCreatedAtBetween(role, start, end);

        response.setInfoMetrics(createMetricList(buildUserMetrics(groupBy, users, role, start, end), String.format("%s audience", role.getType())));
    }

    private Map<String, Long> groupUserMetricsByRating(Role role, ZonedDateTime startMonth, ZonedDateTime endMonth) {
        Map<String, Long> groups = getEmptyRating();

        if(role == Role.BUSINESS) {
            groups.putAll(businessProfileRepository.findByCreatedAtBetween(startMonth, endMonth)
                    .stream()
                    .collect(Collectors.groupingBy(p -> String.format("%.1f", (double) Math.round(p.getRating())), Collectors.counting())));
        } else {
            groups.putAll(profileRepository.findByCreatedAtBetween(startMonth, endMonth)
                    .stream()
                    .collect(Collectors.groupingBy(p -> String.format("%.1f", (double) Math.round(p.getRating())), Collectors.counting())));
        }

        return groups;
    }

    private Map<String, Long> groupUserMetricsByGender(Role role, ZonedDateTime startMonth, ZonedDateTime endMonth) {
        Map<String, Long> groups = analysisService.getGender();

        if(role == Role.BUSINESS) {
            groups.putAll(businessProfileRepository.findByCreatedAtBetween(startMonth, endMonth)
                    .stream()
                    .collect(Collectors.groupingBy(p -> p.getGender().getType(), Collectors.counting())));
        } else {
            groups.putAll(profileRepository.findByCreatedAtBetween(startMonth, endMonth)
                    .stream()
                    .collect(Collectors.groupingBy(p -> p.getGender().getType(), Collectors.counting())));
        }

        return groups;
    }

    private Map<String, Long> groupUserMetricsByStatus(Role role, ZonedDateTime startMonth, ZonedDateTime endMonth) {
        Map<String, Long> groups = Arrays.stream(AccountStatus.values())
                .filter(status -> !(role == Role.USER || role == Role.BUSINESS) || (status != AccountStatus.BUSINESS_DEACTIVATED && status != AccountStatus.BUSINESS_DELETED))
                .collect(Collectors.toMap(AccountStatus::getType, g -> 0L, (a, b) -> b, LinkedHashMap::new));

        groups.putAll(accountStatusTrackerRepository.findByRoleAndCreatedAtBetween(role, startMonth, endMonth)
                .stream()
                .collect(Collectors.groupingBy(p -> p.getStatus().getType(), Collectors.counting())));

        return groups;
    }

    private Map<String, Long> groupUserMetricsByTrip(Role role, ZonedDateTime startMonth, ZonedDateTime endMonth) {
        Map<String, Long> groups = Arrays.stream(ProviderStatus.values())
                .collect(Collectors.toMap(ProviderStatus::getType, g -> 0L, (a, b) -> b, LinkedHashMap::new));

        groups.putAll(activeRepository.findByRoleAndCreatedAtBetween(role, startMonth, endMonth)
                .stream()
                .collect(Collectors.groupingBy(p -> p.getStatus().getType(), Collectors.counting())));

        return groups;
    }

    private Map<String, Long> groupUserMetricsByCertificate(List<User> users) {
        Map<String, Long> groups = new HashMap<>();
        groups.put("Certified", 0L);
        groups.put("Not Certified", 0L);

        for (User user : users) {
            boolean isCertified = certificateRepository.existsByUser(user.getId());

            if (isCertified) {
                groups.put("Certified", groups.get("Certified") + 1);
            } else {
                groups.put("Not Certified", groups.get("Not Certified") + 1);
            }
        }

        return groups;
    }

    private Map<String, Long> groupUserMetricsByCategory(Role role, ZonedDateTime startMonth, ZonedDateTime endMonth) {
        Map<String, Long> groups;

        if(role == Role.BUSINESS) {
            groups = analysisService.getBusinessCategory();
            groups.putAll(businessProfileRepository.findByCreatedAtBetween(startMonth, endMonth)
                    .stream()
                    .collect(Collectors.groupingBy(p -> p.getCategory().getType(), Collectors.counting())));

        } else {
            groups = analysisService.getProfileCategory();
            groups.putAll(profileRepository.findByRoleAndCreatedAtBetween(role, startMonth, endMonth)
                    .stream()
                    .collect(Collectors.groupingBy(p -> p.getCategory().getType(), Collectors.counting())));

        }

        return groups;
    }

    private List<ChartMetric> createMetricList(Map<String, Long> groupedData, String description) {
        return groupedData.entrySet().stream().map(entry -> {
            String key = entry.getKey() == null || entry.getKey().isEmpty() ? "Unknown" : entry.getKey();
            return analysisService.getMetric(entry.getValue(), key, String.format("%s from %s", description, key));
        }).toList();
    }
}