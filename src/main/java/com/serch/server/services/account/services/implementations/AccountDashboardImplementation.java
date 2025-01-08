package com.serch.server.services.account.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.schedule.ScheduleStatus;
import com.serch.server.exceptions.account.AccountException;
import com.serch.server.models.account.BusinessProfile;
import com.serch.server.models.account.Profile;
import com.serch.server.repositories.account.BusinessProfileRepository;
import com.serch.server.repositories.account.ProfileRepository;
import com.serch.server.repositories.rating.RatingRepository;
import com.serch.server.repositories.schedule.ScheduleRepository;
import com.serch.server.repositories.transaction.TransactionRepository;
import com.serch.server.repositories.trip.TripRepository;
import com.serch.server.repositories.trip.TripShareRepository;
import com.serch.server.services.account.responses.DashboardResponse;
import com.serch.server.services.account.services.AccountDashboardService;
import com.serch.server.utils.HelperUtil;
import com.serch.server.utils.MoneyUtil;
import com.serch.server.utils.TimeUtil;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.serch.server.enums.auth.Role.USER;

@Service
@RequiredArgsConstructor
public class AccountDashboardImplementation implements AccountDashboardService {
    private final UserUtil userUtil;
    private final ScheduleRepository scheduleRepository;
    private final TransactionRepository transactionRepository;
    private final RatingRepository ratingRepository;
    private final TripRepository tripRepository;
    private final ProfileRepository profileRepository;
    private final BusinessProfileRepository businessProfileRepository;
    private final TripShareRepository tripShareRepository;

    @Override
    public ApiResponse<DashboardResponse> dashboard() {
        return new ApiResponse<>(buildDashboard());
    }

    private DashboardResponse buildDashboard() {
        if(userUtil.getUser().isBusiness()) {
            BusinessProfile business = businessProfileRepository.findById(userUtil.getUser().getId())
                    .orElseThrow(() -> new AccountException("Access denied"));

            return calculateBusinessDashboard(generateBusinessDashboards(business.getAssociates()), business.getAvatar());
        } else {
            return DashboardResponse.builder()
                    .earning(todayEarnings(userUtil.getUser().getId()))
                    .rating(todayRatings(userUtil.getUser().getId()))
                    .schedule(todaySchedules(userUtil.getUser().getId()))
                    .shared(todayShared(userUtil.getUser().getId()))
                    .trip(todayTrips(userUtil.getUser().getId()))
                    .build();
        }
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

    private List<DashboardResponse> generateBusinessDashboards(List<Profile> associates) {
        return associates
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
                .toList();
    }

    private DashboardResponse getEmptyBusinessDashboard(String image) {
        return DashboardResponse.builder()
                .earning(MoneyUtil.formatToNaira(BigDecimal.ZERO))
                .rating("0.0")
                .schedule("0")
                .shared("0")
                .trip("0")
                .avatar(image)
                .name("Cumulative")
                .build();
    }

    private DashboardResponse calculateBusinessDashboard(List<DashboardResponse> list, String image) {
        DashboardResponse cumulative = list.stream().reduce(DashboardResponse.builder().build(), (accumulator, current) -> {
            buildEarning(accumulator, current);
            buildRating(accumulator, current);
            buildSchedule(accumulator, current);
            buildAccumulator(accumulator, current);
            buildTrip(accumulator, current);
            return accumulator;
        });

        cumulative.setAvatar(image);
        cumulative.setName("Cumulative");
        return cumulative;
    }

    private void buildTrip(DashboardResponse accumulator, DashboardResponse current) {
        int accumulatorTrip = parseInt(accumulator.getTrip());
        int currentTrip = parseInt(current.getTrip());

        accumulator.setTrip(Integer.toString(accumulatorTrip + currentTrip));
    }

    private void buildAccumulator(DashboardResponse accumulator, DashboardResponse current) {
        int accumulatorShared = parseInt(accumulator.getShared());
        int currentShared = parseInt(current.getShared());

        accumulator.setShared(Integer.toString(accumulatorShared + currentShared));
    }

    private void buildSchedule(DashboardResponse accumulator, DashboardResponse current) {
        int accumulatorSchedule = parseInt(accumulator.getSchedule());
        int currentSchedule = parseInt(current.getSchedule());

        accumulator.setSchedule(Integer.toString(accumulatorSchedule + currentSchedule));
    }

    private void buildRating(DashboardResponse accumulator, DashboardResponse current) {
        double accumulatorRating = parseDouble(accumulator.getRating());
        double currentRating = parseDouble(current.getRating());

        accumulator.setRating(Double.toString(accumulatorRating + currentRating));
    }

    private void buildEarning(DashboardResponse accumulator, DashboardResponse current) {
        BigDecimal accumulatorEarning = MoneyUtil.parseFromNaira(accumulator.getEarning());
        BigDecimal currentEarning = MoneyUtil.parseFromNaira(current.getEarning());
        BigDecimal totalEarning = accumulatorEarning.add(currentEarning);

        accumulator.setEarning(MoneyUtil.formatToNaira(totalEarning));
    }

    private double parseDouble(String value) {
        return Optional.ofNullable(value).map(Double::parseDouble).orElse(0.0);
    }

    private int parseInt(String value) {
        return Optional.ofNullable(value).map(Integer::parseInt).orElse(0);
    }

    @Override
    public ApiResponse<List<DashboardResponse>> dashboards(Integer page, Integer size) {
        BusinessProfile business = businessProfileRepository.findById(userUtil.getUser().getId())
                .orElseThrow(() -> new AccountException("Access denied"));

        Page<Profile> associates = profileRepository.findActiveAssociatesByBusinessId(userUtil.getUser().getId(), HelperUtil.getPageable(page, size));

        if(associates.hasContent() && !associates.isEmpty()) {
            List<DashboardResponse> list = new ArrayList<>(generateBusinessDashboards(associates.getContent()));
            if(page == 0) {
                list.add(calculateBusinessDashboard(generateBusinessDashboards(business.getAssociates()), business.getCategory().getImage()));
            }

            return new ApiResponse<>(list);
        } else {
            return new ApiResponse<>(List.of(getEmptyBusinessDashboard(business.getCategory().getImage())));
        }
    }
}
