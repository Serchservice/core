package com.serch.server.services.account.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.schedule.ScheduleStatus;
import com.serch.server.enums.transaction.TransactionStatus;
import com.serch.server.repositories.rating.RatingRepository;
import com.serch.server.repositories.schedule.ScheduleRepository;
import com.serch.server.repositories.transaction.TransactionRepository;
import com.serch.server.services.account.responses.ActivityResponse;
import com.serch.server.services.account.responses.RequestResponse;
import com.serch.server.services.account.services.ActivityService;
import com.serch.server.utils.MoneyUtil;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class ActivityImplementation implements ActivityService {
    private final UserUtil userUtil;
    private final ScheduleRepository scheduleRepository;
    private final TransactionRepository transactionRepository;
    private final RatingRepository ratingRepository;

    private String todaySchedules() {
        return String.valueOf(
                scheduleRepository.today(userUtil.getUser().getId())
                        .stream()
                        .filter(schedule -> schedule.getStatus() == ScheduleStatus.ACCEPTED)
                        .filter(schedule -> schedule.getCreatedAt().isBefore(LocalDateTime.now().plusDays(1)))
                        .toList()
                        .size()
        );
    }

    private String todayEarnings() {
        return MoneyUtil.formatToNaira(
                transactionRepository.totalToday(
                        String.valueOf(userUtil.getUser().getId()), TransactionStatus.SUCCESSFUL,
                        LocalDateTime.of(LocalDate.now(), LocalTime.MIN),
                        LocalDateTime.of(LocalDate.now(), LocalTime.MAX)
                )
        );
    }

    private String todayRatings() {
        Double rating = ratingRepository.todayAverage(String.valueOf(userUtil.getUser().getId()));
        return String.valueOf(rating == null ? 0.0 : rating);
    }

    private String todayTrips() {
        return "";
//        return String.valueOf(
//                tripRepository.findTodayTrips(
//                        userUtil.loggedInUserId(), ConnectionStatus.LEFT,
//                        ConnectionStatus.NOT_ACCEPTED, ConnectionStatus.COMPLETED
//                ).size()
//        );
    }

    private String todayShared() {
        return "";
//        return String.valueOf(
//                tripRepository.findSharedTrips(
//                        userUtil.loggedInUserId(), ConnectionStatus.LEFT,
//                        ConnectionStatus.NOT_ACCEPTED, ConnectionStatus.COMPLETED
//                ).size()
//        );
    }

    @Override
    public ApiResponse<ActivityResponse> today() {
        return new ApiResponse<>(
                ActivityResponse.builder()
                        .earning(todayEarnings())
                        .rating(todayRatings())
                        .schedule(todaySchedules())
                        .shared(todayShared())
                        .trip(todayTrips())
                        .build()
        );
    }

    @Override
    public ApiResponse<RequestResponse> requests() {
        return null;
    }
}
