package com.serch.server.services.schedule.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.schedule.ScheduleStatus;
import com.serch.server.exceptions.others.ScheduleException;
import com.serch.server.models.account.Profile;
import com.serch.server.models.schedule.Schedule;
import com.serch.server.repositories.account.ProfileRepository;
import com.serch.server.repositories.schedule.ScheduleRepository;
import com.serch.server.services.schedule.requests.ScheduleDeclineRequest;
import com.serch.server.services.schedule.requests.ScheduleRequest;
import com.serch.server.services.schedule.responses.ScheduleResponse;
import com.serch.server.services.schedule.responses.ScheduleTimeResponse;
import com.serch.server.services.transaction.services.SchedulePayService;
import com.serch.server.utils.MoneyUtil;
import com.serch.server.utils.TimeUtil;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Implementation of the ScheduleService interface responsible for managing schedules.
 *
 * @see ScheduleService
 * @see SchedulePayService
 * @see UserUtil
 * @see ProfileRepository
 * @see ScheduleRepository
 */
@Service
@RequiredArgsConstructor
public class ScheduleImplementation implements ScheduleService {
    private final SchedulePayService payService;
    private final UserUtil userUtil;
    private final ProfileRepository profileRepository;
    private final ScheduleRepository scheduleRepository;

    @Value("${application.account.schedule.close.limit}")
    private Integer ACCOUNT_SCHEDULE_CLOSE_LIMIT;
    @Value("${application.account.schedule.close.charge}")
    private Integer ACCOUNT_SCHEDULE_CLOSE_CHARGE;
    @Value("${application.account.schedule.close.duration}")
    private Integer ACCOUNT_SCHEDULE_CLOSE_DURATION;

    @Override
    public ApiResponse<String> request(ScheduleRequest request) {
        Profile provider = profileRepository.findById(request.getProvider())
                .orElseThrow(() -> new ScheduleException("Provider not found"));
        Profile user = profileRepository.findById(userUtil.getUser().getId())
                .orElseThrow(() -> new ScheduleException("User not found"));

        LocalDate today = LocalDate.now();
        List<Schedule> schedules = scheduleRepository.findByProvider_SerchIdAndCreatedAtBetween(
                provider.getSerchId(), today.atStartOfDay(),
                today.atTime(23, 59, 59)
        );
        if(schedules.stream().anyMatch(schedule -> schedule.getTime().equalsIgnoreCase(request.getTime()))) {
            throw new ScheduleException("Provider is already booked for %s today".formatted(request.getTime()));
        } else {
            Schedule schedule = new Schedule();
            schedule.setProvider(provider);
            schedule.setUser(user);
            schedule.setStatus(ScheduleStatus.PENDING);
            schedule.setTime(request.getTime().toUpperCase());
            scheduleRepository.save(schedule);

            /// TODO:: Send notification

            return new ApiResponse<>(
                    "Schedule placed for %s. ".formatted(request.getTime()) +
                            "%s will be notified".formatted(schedule.getProvider().getFullName()),
                    HttpStatus.CREATED
            );
        }
    }

    @Override
    public ApiResponse<String> accept(String id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ScheduleException("Schedule not found"));
        if(schedule.getProvider().isSameAs(userUtil.getUser().getId())) {
            if(schedule.getStatus() == ScheduleStatus.PENDING) {
                schedule.setStatus(ScheduleStatus.ACCEPTED);
                schedule.setUpdatedAt(LocalDateTime.now());
                scheduleRepository.save(schedule);
                return new ApiResponse<>(
                        "Schedule accepted. %s will be notified".formatted(schedule.getUser().getFullName()),
                        HttpStatus.OK
                );
            } else {
                throw new ScheduleException("Schedule is already accepted");
            }
        } else {
            throw new ScheduleException("Cannot perform any action on this. Permission Denied");
        }
    }

    @Override
    public ApiResponse<String> cancel(String id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ScheduleException("Schedule not found"));
        if(schedule.getUser().isSameAs(userUtil.getUser().getId())) {
            if(schedule.getStatus() == ScheduleStatus.PENDING) {
                schedule.setStatus(ScheduleStatus.CANCELLED);
                schedule.setUpdatedAt(LocalDateTime.now());
                scheduleRepository.save(schedule);
                return new ApiResponse<>(
                        "Schedule cancelled. %s will not be notified".formatted(
                                schedule.getProvider().getFullName()
                        ),
                        HttpStatus.OK
                );
            } else {
                throw new ScheduleException("Can only cancel pending schedules");
            }
        } else {
            throw new ScheduleException("Cannot perform any action on this. Permission Denied");
        }
    }

    @Override
    public ApiResponse<String> close(String id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ScheduleException("Schedule not found"));
        if(schedule.getUser().isSameAs(userUtil.getUser().getId()) || schedule.getProvider().isSameAs(userUtil.getUser().getId())) {
            if(schedule.getStatus() == ScheduleStatus.ACCEPTED) {
                long diff = getDiff(schedule);

                System.out.printf("Schedule - %s%n", diff);
                schedule.setStatus(ScheduleStatus.CLOSED);
                schedule.setUpdatedAt(LocalDateTime.now());
                schedule.setClosedAt(TimeUtil.formatTimeDifference(diff));
                schedule.setClosedBy(userUtil.getUser().getId());
                schedule.setClosedOnTime(diff <= ACCOUNT_SCHEDULE_CLOSE_DURATION && diff >= 0);
                scheduleRepository.save(schedule);

                int closed = scheduleRepository.findByClosedByAndClosedOnTime(userUtil.getUser().getId(), false).size();
                if(closed == ACCOUNT_SCHEDULE_CLOSE_LIMIT) {
                    return new ApiResponse<>(
                            "Schedule ended. " +
                                    "Do note that you have exceeded your schedule limit and as such " +
                                    "you will be charged %s for any schedule you close from this moment."
                                            .formatted(MoneyUtil.formatToNaira(BigDecimal.valueOf(ACCOUNT_SCHEDULE_CLOSE_CHARGE))),
                            HttpStatus.OK
                    );
                } else {
                    if(closed > ACCOUNT_SCHEDULE_CLOSE_LIMIT && (diff <= ACCOUNT_SCHEDULE_CLOSE_DURATION && diff >= 0)) {
                        ApiResponse<Boolean> response = payService.charge(schedule);
                        if(response.getData()) {
                            return new ApiResponse<>(
                                    "Schedule ended. " +
                                            "Do note that %s will be deducted from your wallet"
                                                    .formatted(MoneyUtil.formatToNaira(BigDecimal.valueOf(ACCOUNT_SCHEDULE_CLOSE_CHARGE))),
                                    HttpStatus.OK
                            );
                        } else {
                            return new ApiResponse<>(
                                    "Schedule ended. " +
                                            "Do note that you have a pending %s that will be deducted from your wallet"
                                                    .formatted(MoneyUtil.formatToNaira(BigDecimal.valueOf(ACCOUNT_SCHEDULE_CLOSE_CHARGE))),
                                    HttpStatus.OK
                            );
                        }
                    } else if(diff <= ACCOUNT_SCHEDULE_CLOSE_DURATION && diff >= 0) {
                        return new ApiResponse<>(
                                "Schedule ended. You have %s remaining".formatted(ACCOUNT_SCHEDULE_CLOSE_LIMIT - closed) +
                                        " times you can cancel a schedule that is within %s to initiate. ".formatted(ACCOUNT_SCHEDULE_CLOSE_DURATION) +
                                        "Always cancel on time if you feel like.",
                                HttpStatus.OK
                        );
                    } else {
                        return new ApiResponse<>("Schedule ended.", HttpStatus.OK);
                    }
                }
            } else {
                throw new ScheduleException("Can only close accepted schedules");
            }
        } else {
            throw new ScheduleException("Cannot perform any action on this. Permission Denied");
        }
    }

    private long getDiff(Schedule schedule) {
        LocalTime currentTime = LocalTime.now();
        LocalTime scheduleTime = LocalTime.parse(schedule.getTime(), DateTimeFormatter.ofPattern("h:mma"));
        return ChronoUnit.MINUTES.between(currentTime, scheduleTime);
    }

    @Override
    public ApiResponse<String> start(String id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ScheduleException("Schedule not found"));

        if(schedule.getProvider().isSameAs(userUtil.getUser().getId()) || schedule.getUser().isSameAs(userUtil.getUser().getId())) {
            long diff = getDiff(schedule);
            schedule.setClosedAt(TimeUtil.formatTimeDifference(diff));
            schedule.setStatus(ScheduleStatus.ATTENDED);
        }
        return null;
    }

    @Override
    public ApiResponse<String> decline(ScheduleDeclineRequest request) {
        Schedule schedule = scheduleRepository.findById(request.getId())
                .orElseThrow(() -> new ScheduleException("Schedule not found"));
        if(request.getReason() == null || request.getReason().isEmpty()) {
            throw new ScheduleException("Reason for declining is required");
        } else {
            if(schedule.getProvider().isSameAs(userUtil.getUser().getId())) {
                if(schedule.getStatus() == ScheduleStatus.PENDING) {
                    schedule.setStatus(ScheduleStatus.DECLINED);
                    schedule.setUpdatedAt(LocalDateTime.now());
                    scheduleRepository.save(schedule);
                    return new ApiResponse<>(
                            "Schedule declined. %s will not be notified".formatted(
                                    schedule.getUser().getFullName()
                            ),
                            HttpStatus.OK
                    );
                } else {
                    throw new ScheduleException("Can only decline pending schedules");
                }
            } else {
                throw new ScheduleException("Cannot perform any action on this. Permission Denied");
            }
        }
    }

    @Override
    public ApiResponse<List<ScheduleResponse>> today() {
        List<ScheduleResponse> list = scheduleRepository.today(userUtil.getUser().getId())
                .stream()
                .sorted(Comparator.comparing(Schedule::getUpdatedAt))
                .map(this::getScheduleResponse)
                .toList();
        return new ApiResponse<>(list);
    }

    private ScheduleResponse getScheduleResponse(Schedule schedule) {
        ScheduleResponse response = new ScheduleResponse();
        response.setId(schedule.getId());
        response.setTime(schedule.getTime());
        response.setName(schedule.getUser().getFullName());
        response.setAvatar(schedule.getUser().getAvatar());
        response.setCategory(schedule.getProvider().getCategory().getType());
        response.setProviderName(schedule.getProvider().getFullName());
        response.setProviderAvatar(schedule.getProvider().getAvatar());
        return response;
    }

    @Override
    public ApiResponse<List<ScheduleResponse>> schedules() {
        List<ScheduleResponse> list = scheduleRepository.schedules(userUtil.getUser().getId())
                .stream()
                .sorted(Comparator.comparing(Schedule::getUpdatedAt))
                .map(this::getScheduleResponse)
                .toList();
        return new ApiResponse<>(list);
    }

    @Override
    public ApiResponse<List<ScheduleTimeResponse>> times(UUID id) {
        LocalDate today = LocalDate.now();
        LocalDateTime currentTime = LocalDateTime.now();
        List<Schedule> schedules = scheduleRepository.findByProvider_SerchIdAndCreatedAtBetween(
                id, today.atStartOfDay(),
                today.atTime(23, 59, 59)
        );

        List<ScheduleTimeResponse> list = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            LocalDateTime currentHour = currentTime
                    .withHour(i % 12)
                    .withMinute(0)
                    .withSecond(0)
                    .with(ChronoField.AMPM_OF_DAY, i < 12 ? 0 : 1);
            response(currentHour, schedules, i, list);
        }

        if(!list.isEmpty()) {
            list.sort((a, b) -> {
                // Convert the time strings to LocalTime objects
                LocalTime timeA = LocalTime.parse(a.getTime(), DateTimeFormatter.ofPattern("h:mma"));
                LocalTime timeB = LocalTime.parse(b.getTime(), DateTimeFormatter.ofPattern("h:mma"));
                // Compare the LocalTime objects
                return timeA.compareTo(timeB);
            });
        }
        return new ApiResponse<>(list);
    }

    private void response(LocalDateTime current, List<Schedule> schedules, int i, List<ScheduleTimeResponse> list) {
        ScheduleTimeResponse response = new ScheduleTimeResponse();
        response.setTime(current.format(DateTimeFormatter.ofPattern("h:mma")));
        response.setIsAmTaken(isTimeTaken(current, schedules, i < 12));
        response.setIsPmTaken(isTimeTaken(current, schedules, i > 12));
        list.add(response);
    }

    private boolean isTimeTaken(LocalDateTime currentTime, List<Schedule> schedules, boolean isPM) {
        return schedules.stream().anyMatch(schedule -> {
            LocalDateTime time = LocalDateTime.parse(schedule.getTime(), DateTimeFormatter.ofPattern("h:mma"));
            LocalDateTime scheduled;
            if (isPM && time.getHour() < 12) {
                scheduled = time.plusHours(12);
            } else {
                scheduled = time;
            }
            return scheduled.equals(currentTime);
        });
    }

    @Override
    public void notifySchedules() {
        LocalDate today = LocalDate.now();
        scheduleRepository.findByCreatedAtBetween(today.atStartOfDay(), today.atTime(23, 59, 59))
                .forEach(schedule -> {
                    LocalTime currentTime = LocalTime.now();
                    LocalTime time = LocalTime.parse(schedule.getTime(), DateTimeFormatter.ofPattern("h:mma"));
                    if(time.equals(currentTime)) {
                        /// TODO:: Send notification to both scheduler and scheduled
                    }
                });
    }

    @Override
    public void closePastUnaccepted() {
        LocalDateTime current = LocalDateTime.now();
        scheduleRepository.findByStatusAndCreatedAtBefore(ScheduleStatus.PENDING, current)
                .forEach(schedule -> {
                    schedule.setStatus(ScheduleStatus.UNATTENDED);
                    schedule.setUpdatedAt(current);
                    schedule.setClosedAt(TimeUtil.formatTimeDifference(getDiff(schedule)));
                    scheduleRepository.save(schedule);
                });
    }
}
