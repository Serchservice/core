package com.serch.server.services.account.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.schedule.ScheduleStatus;
import com.serch.server.exceptions.account.AccountException;
import com.serch.server.models.auth.User;
import com.serch.server.models.account.BusinessProfile;
import com.serch.server.models.shared.Guest;
import com.serch.server.repositories.account.ProfileRepository;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.repositories.account.BusinessProfileRepository;
import com.serch.server.repositories.conversation.CallRepository;
import com.serch.server.repositories.rating.RatingRepository;
import com.serch.server.repositories.schedule.ScheduleRepository;
import com.serch.server.repositories.shared.GuestRepository;
import com.serch.server.repositories.transaction.TransactionRepository;
import com.serch.server.repositories.trip.TripRepository;
import com.serch.server.repositories.trip.TripShareRepository;
import com.serch.server.services.account.responses.AccountResponse;
import com.serch.server.services.account.responses.DashboardResponse;
import com.serch.server.services.account.services.AccountService;
import com.serch.server.services.shared.services.GuestService;
import com.serch.server.services.shared.services.SharedService;
import com.serch.server.utils.MoneyUtil;
import com.serch.server.utils.TimeUtil;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.serch.server.enums.auth.Role.USER;

/**
 * This is the class that contains the logic and implementation of its wrapper class {@link AccountService}.
 *
 * @see GuestService
 * @see CallRepository
 * @see ProfileRepository
 * @see PasswordEncoder
 * @see GuestRepository
 * @see UserRepository
 * @see TripRepository
 */
@Service
@RequiredArgsConstructor
public class AccountImplementation implements AccountService {
    private final UserUtil userUtil;
    private final ScheduleRepository scheduleRepository;
    private final TransactionRepository transactionRepository;
    private final RatingRepository ratingRepository;
    private final TripRepository tripRepository;
    private final SharedService sharedService;
    private final GuestRepository guestRepository;
    private final BusinessProfileRepository businessProfileRepository;
    private final ProfileRepository profileRepository;
    private final TripShareRepository tripShareRepository;
    private final UserRepository userRepository;

    @Override
    public ApiResponse<List<AccountResponse>> accounts() {
        Guest guest = guestRepository.findByEmailAddressIgnoreCase(UserUtil.getLoginUser())
                .orElse(null);
        User user = userUtil.getUser();

        if(user.isProfile()) {
            return sharedService.buildAccountResponse(guest, user);
        } else {
            throw new AccountException("Access denied. Your account cannot perform this action.");
        }
    }

    @Override
    public ApiResponse<String> lastPasswordUpdateAt() {
        String time = TimeUtil.formatDay(userUtil.getUser().getLastUpdatedAt(), userUtil.getUser().getTimezone());
        return new ApiResponse<>("Success", time, HttpStatus.OK);
    }

    private String todaySchedules(UUID id) {
        return String.valueOf(
                scheduleRepository.active(id)
                        .stream()
                        .filter(schedule -> schedule.getStatus() == ScheduleStatus.ACCEPTED)
                        .filter(schedule -> schedule.getCreatedAt().isBefore(TimeUtil.now().plusDays(1)))
                        .toList()
                        .size()
        );
    }

    private String todayEarnings(UUID id) {
        return MoneyUtil.formatToNaira(
                transactionRepository.totalToday(
                        String.valueOf(id),
                        ZonedDateTime.of(LocalDateTime.of(LocalDate.now(), LocalTime.MIN), ZoneOffset.UTC),
                        ZonedDateTime.of(LocalDateTime.of(LocalDate.now(), LocalTime.MAX), ZoneOffset.UTC)
                )
        );
    }

    private String todayRatings(UUID id) {
        Double rating = ratingRepository.todayAverage(String.valueOf(id));
        return String.valueOf(rating == null ? 0.0 : rating);
    }

    private String todayTrips(UUID id) {
        int size = userUtil.getUser().getRole() == USER
                ? tripRepository.todayTrips(String.valueOf(id)).size()
                : tripRepository.todayTrips(id).size();
        return String.valueOf(size);
    }

    private String todayShared(UUID id) {
        return String.valueOf(tripShareRepository.todaySharedTrips(id).size());
    }

    @Override
    public ApiResponse<DashboardResponse> dashboard() {
        return new ApiResponse<>(
                DashboardResponse.builder()
                        .earning(todayEarnings(userUtil.getUser().getId()))
                        .rating(todayRatings(userUtil.getUser().getId()))
                        .schedule(todaySchedules(userUtil.getUser().getId()))
                        .shared(todayShared(userUtil.getUser().getId()))
                        .trip(todayTrips(userUtil.getUser().getId()))
                        .build()
        );
    }

    @Override
    public ApiResponse<List<DashboardResponse>> dashboards() {
        BusinessProfile business = businessProfileRepository.findById(userUtil.getUser().getId())
                .orElseThrow(() -> new AccountException("Access denied"));
        if(business.getAssociates() != null && !business.getAssociates().isEmpty()) {
            List<DashboardResponse> list = new ArrayList<>(business.getAssociates()
                    .stream()
                    .map(profile -> DashboardResponse.builder()
                            .earning(todayEarnings(profile.getId()))
                            .rating(todayRatings(profile.getId()))
                            .schedule(todaySchedules(profile.getId()))
                            .shared(todayShared(profile.getId()))
                            .trip(todayTrips(profile.getId()))
                            .avatar(profile.getAvatar())
                            .name(profile.getFullName())
                            .build())
                    .toList());

            // Calculate cumulative values
            DashboardResponse cumulative = list.stream().reduce(DashboardResponse.builder().build(), (accumulator, current) -> {
                BigDecimal accumulatorEarning = MoneyUtil.parseFromNaira(accumulator.getEarning());
                BigDecimal currentEarning = MoneyUtil.parseFromNaira(current.getEarning());
                BigDecimal totalEarning = accumulatorEarning.add(currentEarning);
                accumulator.setEarning(MoneyUtil.formatToNaira(totalEarning));

                double accumulatorRating = parseDouble(accumulator.getRating());
                double currentRating = parseDouble(current.getRating());
                accumulator.setRating(Double.toString(accumulatorRating + currentRating));

                int accumulatorSchedule = parseInt(accumulator.getSchedule());
                int currentSchedule = parseInt(current.getSchedule());
                accumulator.setSchedule(Integer.toString(accumulatorSchedule + currentSchedule));

                int accumulatorShared = parseInt(accumulator.getShared());
                int currentShared = parseInt(current.getShared());
                accumulator.setShared(Integer.toString(accumulatorShared + currentShared));

                int accumulatorTrip = parseInt(accumulator.getTrip());
                int currentTrip = parseInt(current.getTrip());
                accumulator.setTrip(Integer.toString(accumulatorTrip + currentTrip));

                return accumulator;
            });
            cumulative.setAvatar(business.getCategory().getImage());
            cumulative.setName("Cumulative");
            list.add(cumulative);
            return new ApiResponse<>(list);
        }
        return new ApiResponse<>(List.of(DashboardResponse.builder()
                .earning(MoneyUtil.formatToNaira(BigDecimal.ZERO))
                .rating("0.0")
                .schedule("0")
                .shared("0")
                .trip("0")
                .avatar(business.getAvatar())
                .name("Cumulative")
                .build()
        ));
    }

    @Override
    public ApiResponse<String> updateFcmToken(String token) {
        profileRepository.findById(userUtil.getUser().getId()).ifPresentOrElse(profile -> {
            profile.setFcmToken(token);
            profileRepository.save(profile);
            }, () -> businessProfileRepository.findById(userUtil.getUser().getId()).ifPresent(business -> {
                business.setFcmToken(token);
                businessProfileRepository.save(business);
            })
        );
        return new ApiResponse<>("Successfully updated FCM token", HttpStatus.OK);
    }

    private double parseDouble(String value) {
        return Optional.ofNullable(value)
                .map(Double::parseDouble)
                .orElse(0.0);
    }

    private int parseInt(String value) {
        return Optional.ofNullable(value)
                .map(Integer::parseInt)
                .orElse(0);
    }

    @Override
    public ApiResponse<String> updateTimezone(String timezone) {
        profileRepository.findById(userUtil.getUser().getId()).ifPresentOrElse(profile -> {
                    profile.getUser().setTimezone(timezone);
                    userRepository.save(profile.getUser());
                }, () -> businessProfileRepository.findById(userUtil.getUser().getId()).ifPresent(business -> {
                    business.getUser().setTimezone(timezone);
                    userRepository.save(business.getUser());
                })
        );

        return new ApiResponse<>("Successfully updated timezone", HttpStatus.OK);
    }
}