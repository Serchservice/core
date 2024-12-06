package com.serch.server.admin.services.scopes.account.services.implementation;

import com.serch.server.admin.services.responses.ChartMetric;
import com.serch.server.admin.services.scopes.account.responses.AccountCountrySectionResponse;
import com.serch.server.admin.services.scopes.account.responses.AccountScopeMoreAnalysisResponse;
import com.serch.server.admin.services.scopes.account.services.AccountScopeMoreAnalysisService;
import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.account.Gender;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.enums.auth.Role;
import com.serch.server.models.account.BusinessProfile;
import com.serch.server.models.account.Profile;
import com.serch.server.models.auth.User;
import com.serch.server.models.shared.Guest;
import com.serch.server.repositories.account.BusinessProfileRepository;
import com.serch.server.repositories.account.ProfileRepository;
import com.serch.server.repositories.auth.SessionRepository;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.repositories.shared.GuestRepository;
import com.serch.server.utils.AdminUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountScopeMoreAnalysisImplementation implements AccountScopeMoreAnalysisService {
    private final GuestRepository guestRepository;
    private final BusinessProfileRepository businessProfileRepository;
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;

    @Override
    public ApiResponse<AccountScopeMoreAnalysisResponse> fetch(Role role) {
        return new ApiResponse<>(getResponse(role, null, null, "Overview"));
    }

    private AccountScopeMoreAnalysisResponse getResponse(Role role, ZonedDateTime start, ZonedDateTime end, String section) {
        AccountScopeMoreAnalysisResponse response = new AccountScopeMoreAnalysisResponse();
        response.setDemographics(getDemographics(role, start, end));
        response.setGeographics(getGeographics(role, start, end));
        response.setStates(getStates(role, start, end));
        response.setCategories(getCategories(role, start, end));
        response.setActivities(getActivities(role, start, end));
        response.setOverview(getOverview(role, start, end));
        response.setCountries(getCountries(role, start, end));
        response.setSection(section);

        return response;
    }

    private List<ChartMetric> getDemographics(Role role, ZonedDateTime start, ZonedDateTime end) {
        Map<String, Double> data;

        if(role != null) {
            if(role == Role.BUSINESS) {
                if(start != null && end != null) {
                    data = buildBusiness(businessProfileRepository.findByCreatedAtBetween(start, end), "gender");
                } else if(start != null) {
                    data = buildBusiness(businessProfileRepository.findByCreatedAtBetween(start, start.plusYears(1)), "gender");
                } else {
                    data = buildBusiness(businessProfileRepository.findAll(), "gender");
                }
            } else {
                if(start != null && end != null) {
                    data = buildProfile(profileRepository.findByCreatedAtBetween(start, end), "gender");
                } else if(start != null) {
                    data = buildProfile(profileRepository.findByCreatedAtBetween(start, start.plusYears(1)), "gender");
                } else {
                    data = buildProfile(profileRepository.findAll(), "gender");
                }
            }
        } else {
            if(start != null && end != null) {
                data = buildGuestResponse(guestRepository.findByCreatedAtBetween(start, end), "gender");
            } else if(start != null) {
                data = buildGuestResponse(guestRepository.findByCreatedAtBetween(start, start.plusYears(1)), "gender");
            } else {
                data = buildGuestResponse(guestRepository.findAll(), "gender");
            }
        }

        return getMetrics(data);
    }

    private Map<String, Double> buildBusiness(List<BusinessProfile> accounts, String group) {
        Map<String, Long> groups;

        if(group.equalsIgnoreCase("gender")) {
            groups = getGender();
            groups.putAll(accounts.stream().collect(Collectors.groupingBy(p -> p.getGender().getType(), Collectors.counting())));
        } else {
            groups = getBusinessCategory();
            groups.putAll(accounts.stream().collect(Collectors.groupingBy(p -> p.getCategory().getType(), Collectors.counting())));
        }

        return getPercentage(accounts.size(), groups);
    }

    private Map<String, Double> getPercentage(long accounts, Map<String, Long> groups) {
        return groups.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> getPercentage(accounts, entry.getValue())));
    }

    private Double getPercentage(long total, long value) {
        return total > 0 ? (value * 100.0) / total : 0.0;
    }

    private Map<String, Double> buildProfile(List<Profile> profiles, String group) {
        Map<String, Long> groups;

        if(group.equalsIgnoreCase("gender")) {
            groups = getGender();
            groups.putAll(profiles.stream().collect(Collectors.groupingBy(p -> p.getGender().getType(), Collectors.counting())));
        } else {
            groups = getProfileCategory();
            groups.putAll(profiles.stream().collect(Collectors.groupingBy(p -> p.getCategory().getType(), Collectors.counting())));
        }

        return getPercentage(profiles.size(), groups);
    }

    private Map<String, Double> buildGuestResponse(List<Guest> guests, String group) {
        if(group.equalsIgnoreCase("gender")) {
            Map<String, Long> groups = getGender();
            groups.putAll(guests.stream().collect(Collectors.groupingBy(p -> p.getGender().getType(), Collectors.counting())));

            return getPercentage(guests.size(), groups);
        } else if(group.equalsIgnoreCase("country")) {
            return getPercentage(guests.size(), guests.stream().collect(Collectors.groupingBy(Guest::getCountry, Collectors.counting())));
        } else {
            return getPercentage(guests.size(), guests.stream().collect(Collectors.groupingBy(Guest::getState, Collectors.counting())));
        }
    }

    private List<ChartMetric> getMetrics(Map<String, Double> data) {
        return data.entrySet().stream().map(entry -> {
            String key = entry.getKey() == null || entry.getKey().isEmpty() ? "Unknown" : entry.getKey();
            return getMetric(0L, key, String.valueOf(entry.getValue()));
        }).toList();
    }

    private List<ChartMetric> getGeographics(Role role, ZonedDateTime start, ZonedDateTime end) {
        Map<String, Double> data;

        if(role != null) {
            if(start != null && end != null) {
                data = buildUserResponse(userRepository.findByRoleAndCreatedAtBetween(role, start, end), "country");
            } else if(start != null) {
                data = buildUserResponse(userRepository.findByRoleAndCreatedAtBetween(role, start, start.plusYears(1)), "country");
            } else {
                data = buildUserResponse(userRepository.findByRole(role), "country");
            }
        } else {
            if(start != null && end != null) {
                data = buildGuestResponse(guestRepository.findByCreatedAtBetween(start, end), "country");
            } else if(start != null) {
                data = buildGuestResponse(guestRepository.findByCreatedAtBetween(start, start.plusYears(1)), "country");
            } else {
                data = buildGuestResponse(guestRepository.findAll(), "country");
            }
        }

        return getMetrics(data);
    }

    private Map<String, Double> buildUserResponse(List<User> users, String group) {
        if(group.equalsIgnoreCase("country")) {
            return getPercentage(users.size(), users.stream().collect(Collectors.groupingBy(User::getCountry, Collectors.counting())));
        } else {
            return getPercentage(users.size(), users.stream().collect(Collectors.groupingBy(User::getState, Collectors.counting())));
        }
    }

    private List<ChartMetric> getStates(Role role, ZonedDateTime start, ZonedDateTime end) {
        Map<String, Double> data;

        if(role != null) {
            if(start != null && end != null) {
                data = buildUserResponse(userRepository.findByRoleAndCreatedAtBetween(role, start, end), "state");
            } else if(start != null) {
                data = buildUserResponse(userRepository.findByRoleAndCreatedAtBetween(role, start, start.plusYears(1)), "state");
            } else {
                data = buildUserResponse(userRepository.findByRole(role), "state");
            }
        } else {
            if(start != null && end != null) {
                data = buildGuestResponse(guestRepository.findByCreatedAtBetween(start, end), "state");
            } else if(start != null) {
                data = buildGuestResponse(guestRepository.findByCreatedAtBetween(start, start.plusYears(1)), "state");
            } else {
                data = buildGuestResponse(guestRepository.findAll(), "state");
            }
        }

        return getMetrics(data);
    }

    private List<ChartMetric> getCategories(Role role, ZonedDateTime start, ZonedDateTime end) {
        Map<String, Double> data;

        if(role != null) {
            if(role == Role.BUSINESS) {
                if(start != null && end != null) {
                    data = buildBusiness(businessProfileRepository.findByCreatedAtBetween(start, end), "category");
                } else if(start != null) {
                    data = buildBusiness(businessProfileRepository.findByCreatedAtBetween(start, start.plusYears(1)), "category");
                } else {
                    data = buildBusiness(businessProfileRepository.findAll(), "category");
                }
            } else if(role == Role.USER) {
                return new ArrayList<>();
            } else {
                if(start != null && end != null) {
                    data = buildProfile(profileRepository.findByCreatedAtBetween(start, end), "category");
                } else if(start != null) {
                    data = buildProfile(profileRepository.findByCreatedAtBetween(start, start.plusYears(1)), "category");
                } else {
                    data = buildProfile(profileRepository.findAll(), "category");
                }
            }
        } else {
            return new ArrayList<>();
        }

        return getMetrics(data);
    }

    private List<ChartMetric> getActivities(Role role, ZonedDateTime start, ZonedDateTime end) {
        if(role != null) {
            long totalUsers = userRepository.countByRole(role);
            long activeUsers;

            if(start != null && end != null) {
                activeUsers = sessionRepository.countDistinctUsersByRoleAndDateRange(role, start, end);
            } else if(start != null) {
                activeUsers = sessionRepository.countDistinctUsersByRoleAndDateRange(role, start, start.plusYears(1));
            } else {
                activeUsers = sessionRepository.countDistinctUsersByRole(role);
            }

            return getMetrics(getPercentage(totalUsers, Map.of(
                    "Active", activeUsers,
                    "Inactive", totalUsers - activeUsers
            )));
        } else {
            return new ArrayList<>();
        }
    }

    private List<ChartMetric> getOverview(Role role, ZonedDateTime start, ZonedDateTime end) {
        List<ChartMetric> metrics = new ArrayList<>();

        if(role != null) {
            if(start != null && end != null) {
                long size = userRepository.findByRoleAndCreatedAtBetween(role, start, end).size();

                metrics.add(getMetric(0L, String.format("New %s", role.getType()), String.valueOf(size)));
                metrics.add(getGrowthMetric(
                        size,
                        userRepository.findByRoleAndCreatedAtBetween(role, start.minusYears(1), end.minusYears(1)).size()
                ));
            } else if(start != null) {
                long size = userRepository.findByRoleAndCreatedAtBetween(role, start, start.plusYears(1)).size();

                metrics.add(getMetric(0L, String.format("New %s", role.getType()), String.valueOf(size)));
                metrics.add(getGrowthMetric(
                        size,
                        userRepository.findByRoleAndCreatedAtBetween(role, start.minusYears(1), start).size()
                ));
            } else {
                metrics.add(getMetric(0L, String.format("Total %s", role.getType()), String.valueOf(userRepository.findByRole(role).size())));
            }
        } else {
            if(start != null && end != null) {
                long size = guestRepository.findByCreatedAtBetween(start, end).size();

                metrics.add(getMetric(0L, "New guests", String.valueOf(size)));
                metrics.add(getGrowthMetric(size, guestRepository.findByCreatedAtBetween(start.minusYears(1), end.minusYears(1)).size()));
            } else if(start != null) {
                long size = guestRepository.findByCreatedAtBetween(start, start.plusYears(1)).size();

                metrics.add(getMetric(0L, "New guests", String.valueOf(size)));
                metrics.add(getGrowthMetric(size, guestRepository.findByCreatedAtBetween(start.minusYears(1), start).size()));
            } else {
                metrics.add(getMetric(0L, "Total guests", String.valueOf(guestRepository.findAll().size())));
            }
        }

        return metrics;
    }

    private ChartMetric getGrowthMetric(long current, long previous) {
        return getMetric(0L, "Growth", String.format("%.2f%%", getPercentage(previous, (current - previous))));
    }

    private List<AccountCountrySectionResponse> getCountries(Role role, ZonedDateTime start, ZonedDateTime end) {
        List<AccountCountrySectionResponse> countries = new ArrayList<>();

        if(role != null) {
            List<User> users;
            if(start != null && end != null) {
                users = userRepository.findByRoleAndCreatedAtBetween(role, start, end);
            } else if(start != null) {
                users = userRepository.findByRoleAndCreatedAtBetween(role, start, start.plusYears(1));
            } else {
                users = userRepository.findByRole(role);
            }

            users.stream().collect(Collectors.groupingBy(User::getCountry))
                    .forEach((key, value) -> countries.add(getCountry(
                            key,
                            getPercentage(users.size(), value.size()),
                            (long) value.size(),
                            role.getType()
                    )));
        } else {
            List<Guest> guests;
            if(start != null && end != null) {
                guests = guestRepository.findByCreatedAtBetween(start, end);
            } else if(start != null) {
                guests = guestRepository.findByCreatedAtBetween(start, start.plusYears(1));
            } else {
                guests = guestRepository.findAll();
            }

            guests.stream().collect(Collectors.groupingBy(Guest::getCountry))
                    .forEach((key, value) -> countries.add(getCountry(
                            key,
                            getPercentage(guests.size(), value.size()),
                            (long) value.size(),
                            "guests"
                    )));
        }

        return countries;
    }

    private AccountCountrySectionResponse getCountry(String country, Double value, Long total, String type) {
        AccountCountrySectionResponse response = new AccountCountrySectionResponse();
        response.setColors(List.of(AdminUtil.randomColor(), AdminUtil.randomColor()));
        response.setLabel(country);
        response.setValue(value);
        response.setContent(formatContent(total, type));

        return response;
    }

    private String formatContent(Long total, String type) {
        return String.format("%s %s", AdminUtil.formatCount(total), type.toLowerCase());
    }

    @Override
    public ApiResponse<List<AccountScopeMoreAnalysisResponse>> fetch(Role role, Integer year) {
        List<AccountScopeMoreAnalysisResponse> list = new ArrayList<>();

        ZonedDateTime start = AdminUtil.getStartYear(year);
        list.add(getResponse(role, start, null, "Overview"));

        for (int month = 1; month <= 12; month++) {
            ZonedDateTime startMonth = start.withMonth(month);
            ZonedDateTime endMonth = startMonth.plusMonths(1).minusSeconds(1);

            list.add(getResponse(role, startMonth, endMonth, startMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH)));
        }

        return new ApiResponse<>(list);
    }

    @Override
    public Map<String, Long> getGender() {
        return Arrays.stream(Gender.values())
                .filter(gender -> gender != Gender.NONE)
                .collect(Collectors.toMap(Gender::getType, g -> 0L, (a, b) -> b, LinkedHashMap::new));
    }

    @Override
    public Map<String, Long> getBusinessCategory() {
        return Arrays.stream(SerchCategory.values())
                .filter(category -> category != SerchCategory.USER)
                .filter(category -> category != SerchCategory.GUEST)
                .collect(Collectors.toMap(SerchCategory::getType, g -> 0L, (a, b) -> b, LinkedHashMap::new));
    }

    @Override
    public Map<String, Long> getProfileCategory() {
        return Arrays.stream(SerchCategory.values())
                .filter(category -> category != SerchCategory.USER)
                .filter(category -> category != SerchCategory.BUSINESS)
                .filter(category -> category != SerchCategory.GUEST)
                .collect(Collectors.toMap(SerchCategory::getType, g -> 0L, (a, b) -> b, LinkedHashMap::new));
    }

    @Override
    public ChartMetric getMetric(Long count, String id, String info) {
        ChartMetric metric = new ChartMetric();
        metric.setLabel(id);
        metric.setValue(count);
        metric.setColor(AdminUtil.randomColor());
        metric.setImage(info);

        return metric;
    }
}