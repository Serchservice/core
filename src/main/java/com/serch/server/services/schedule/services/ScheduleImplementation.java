package com.serch.server.services.schedule.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.schedule.ScheduleStatus;
import com.serch.server.exceptions.others.ScheduleException;
import com.serch.server.mappers.ScheduleMapper;
import com.serch.server.models.account.Profile;
import com.serch.server.models.schedule.Schedule;
import com.serch.server.repositories.account.ProfileRepository;
import com.serch.server.repositories.schedule.ScheduleRepository;
import com.serch.server.services.schedule.requests.ScheduleDeclineRequest;
import com.serch.server.services.schedule.requests.ScheduleRequest;
import com.serch.server.services.schedule.responses.ScheduleGroupResponse;
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
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static com.serch.server.enums.schedule.ScheduleStatus.*;

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

    private boolean isCurrentUser(UUID id) {
        return userUtil.getUser().getId().equals(id);
    }

    @Override
    public ApiResponse<ScheduleResponse> schedule(ScheduleRequest request) {
        Profile provider = profileRepository.findById(request.getProvider())
                .orElseThrow(() -> new ScheduleException("Provider not found"));
        Profile user = profileRepository.findById(userUtil.getUser().getId())
                .orElseThrow(() -> new ScheduleException("User not found"));

        List<Schedule> schedules = getSchedules(provider.getId());
        validateTime(request);

        // Check if the requested time is already booked
        if (schedules.stream().anyMatch(schedule -> schedule.getTime().equalsIgnoreCase(request.getTime()))) {
            throw new ScheduleException("Provider is already booked for %s today".formatted(request.getTime()));
        } else {
            Schedule schedule = new Schedule();
            schedule.setProvider(provider);
            schedule.setUser(user);
            schedule.setStatus(PENDING);
            schedule.setTime(request.getTime().toUpperCase());
            ScheduleResponse response = response(scheduleRepository.save(schedule));

            /// TODO:: Send notification

            return new ApiResponse<>(
                    "Schedule placed for %s. ".formatted(request.getTime()) +
                            "%s will be notified".formatted(schedule.getProvider().getFullName()),
                    response,
                    HttpStatus.CREATED
            );
        }
    }

    private List<Schedule> getSchedules(UUID provider) {
        LocalDate today = LocalDate.now();
        return scheduleRepository.findByProvider_IdAndCreatedAtBetween(
                provider, today.atStartOfDay(), today.atTime(23, 59, 59)
        );
    }

    private static void validateTime(ScheduleRequest request) {
        LocalDate today = LocalDate.now();
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mma");
        LocalDateTime requestedTime = LocalDateTime.of(today, LocalTime.parse(request.getTime().toUpperCase(), formatter));

        // Check if the requested time has already passed
        if (requestedTime.isBefore(currentTime)) {
            throw new ScheduleException("The requested time %s has already passed.".formatted(request.getTime()));
        }
    }

    @Override
    public ApiResponse<String> accept(String id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ScheduleException("Schedule not found"));

        validateTime(ScheduleRequest.builder().time(schedule.getTime()).build());
        if(schedule.getProvider().isSameAs(userUtil.getUser().getId())) {
            if(schedule.getStatus() == PENDING) {
                schedule.setStatus(ACCEPTED);
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
            if(schedule.getStatus() == PENDING) {
                schedule.setStatus(CANCELLED);
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
        if(isCurrentUser(schedule.getUser().getId()) || isCurrentUser(schedule.getProvider().getId())) {
            if(schedule.getStatus() == ACCEPTED) {
                long diff = getDiff(schedule);

                System.out.printf("Schedule - %s%n", diff);
                schedule.setStatus(ScheduleStatus.CLOSED);
                schedule.setUpdatedAt(LocalDateTime.now());
                schedule.setClosedAt(TimeUtil.formatTimeDifference(diff));
                schedule.setClosedBy(userUtil.getUser().getId());
                schedule.setClosedOnTime(diff <= ACCOUNT_SCHEDULE_CLOSE_DURATION && diff >= 0);
                scheduleRepository.save(schedule);

                /// TODO:: Send notification

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
                        if(payService.charge(schedule)) {
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
                if(schedule.getStatus() == PENDING) {
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
    public ApiResponse<List<ScheduleResponse>> active() {
        List<Schedule> schedules = scheduleRepository.active(userUtil.getUser().getId());
        List<ScheduleResponse> list = new ArrayList<>();

        if(schedules != null && !schedules.isEmpty()) {
            list = schedules.stream()
                    .sorted(Comparator.comparing(Schedule::getUpdatedAt))
                    .map(this::response)
                    .toList();
        }
        return new ApiResponse<>(list);
    }

    private ScheduleResponse response(Schedule schedule) {
        ScheduleResponse response = ScheduleMapper.INSTANCE.response(schedule);
        response.setName(
                isCurrentUser(schedule.getProvider().getId())
                        ? schedule.getUser().getFullName()
                        : schedule.getProvider().getFullName()
        );
        response.setAvatar(
                isCurrentUser(schedule.getProvider().getId())
                        ? schedule.getUser().getAvatar()
                        : schedule.getProvider().getAvatar()
        );
        response.setCategory(
                isCurrentUser(schedule.getProvider().getId())
                        ? schedule.getUser().getCategory().getType()
                        : schedule.getProvider().getCategory().getType()
        );
        response.setImage(
                isCurrentUser(schedule.getProvider().getId())
                        ? schedule.getUser().getCategory().getImage()
                        : schedule.getProvider().getCategory().getImage()
        );
        response.setRating(
                isCurrentUser(schedule.getProvider().getId())
                        ? schedule.getUser().getRating()
                        : schedule.getProvider().getRating()
        );
        response.setLabel(TimeUtil.formatDay(schedule.getCreatedAt()));
        response.setReason(buildReason(schedule));

        if(schedule.getClosedBy() != null) {
            response.setClosedBy(profileRepository.findById(schedule.getClosedBy()).map(Profile::getFullName).orElse(""));
        } else {
            response.setClosedBy("From Serch");
        }
        return response;
    }

    private String buildReason(Schedule schedule) {
        if(userUtil.getUser().isProfile()) {
            if(userUtil.getUser().isProvider()) {
                if(schedule.getStatus() == PENDING) {
                    return String.format(
                            "%s invited you for a scheduled service trip for %s, %s",
                            schedule.getUser().getFirstName(),
                            schedule.getTime(),
                            TimeUtil.formatChatLabel(schedule.getCreatedAt())
                    );
                } else if(schedule.getStatus() == DECLINED) {
                    return schedule.getReason();
                } else if(schedule.getStatus() == UNATTENDED) {
                    return "This schedule was left unattended. There was no trip action initiated after schedule time clocked";
                } else if(schedule.getStatus() == CANCELLED) {
                    return String.format(
                            "%s cancelled your scheduled invitation for %s, %s",
                            schedule.getUser().getFirstName(),
                            schedule.getTime(),
                            TimeUtil.formatChatLabel(schedule.getCreatedAt())
                    );
                } else if(schedule.getStatus() == ACCEPTED) {
                    return String.format(
                            "You accepted %s's scheduled invitation for %s, %s",
                            schedule.getUser().getFirstName(),
                            schedule.getTime(),
                            TimeUtil.formatChatLabel(schedule.getCreatedAt())
                    );
                } else if(schedule.getStatus() == CLOSED) {
                    return String.format(
                            "%s closed this scheduled invitation for %s, %s",
                            profileRepository.findById(schedule.getClosedBy()).map(Profile::getFullName).orElse(""),
                            schedule.getTime(),
                            TimeUtil.formatChatLabel(schedule.getCreatedAt())
                    );
                } else {
                    return String.format(
                            "You %s %s's schedule invitation for %s, %s",
                            schedule.getStatus().getType(),
                            schedule.getUser().getFirstName(),
                            schedule.getTime(),
                            TimeUtil.formatChatLabel(schedule.getCreatedAt())
                    );
                }
            } else {
                if(schedule.getStatus() == PENDING) {
                    return String.format(
                            "You invited %s for a scheduled service trip for %s, %s",
                            schedule.getProvider().getFullName(),
                            schedule.getTime(),
                            TimeUtil.formatChatLabel(schedule.getCreatedAt())
                    );
                } else if(schedule.getStatus() == DECLINED) {
                    return schedule.getReason();
                } else if(schedule.getStatus() == UNATTENDED) {
                    return "This schedule was left unattended. There was no trip action initiated after schedule time clocked";
                } else if(schedule.getStatus() == CANCELLED) {
                    return String.format(
                            "You cancelled %s's scheduled invitation for %s, %s",
                            schedule.getProvider().getFullName(),
                            schedule.getTime(),
                            TimeUtil.formatChatLabel(schedule.getCreatedAt())
                    );
                } else if(schedule.getStatus() == ACCEPTED) {
                    return String.format(
                            "%s accepted your scheduled invitation for %s, %s",
                            schedule.getProvider().getFullName(),
                            schedule.getTime(),
                            TimeUtil.formatChatLabel(schedule.getCreatedAt())
                    );
                } else if(schedule.getStatus() == CLOSED) {
                    return String.format(
                            "%s closed this scheduled invitation for %s, %s",
                            profileRepository.findById(schedule.getClosedBy()).map(Profile::getFullName).orElse(""),
                            schedule.getTime(),
                            TimeUtil.formatChatLabel(schedule.getCreatedAt())
                    );
                } else {
                    return String.format(
                            "%s %s %s's schedule invitation for %s, %s",
                            schedule.getProvider().getFullName(),
                            schedule.getStatus().getType(),
                            schedule.getUser().getFirstName(),
                            schedule.getTime(),
                            TimeUtil.formatChatLabel(schedule.getCreatedAt())
                    );
                }
            }
        } else {
            if(schedule.getStatus() == PENDING) {
                return String.format(
                        "%s invited %s for a scheduled service trip for %s, %s",
                        schedule.getUser().getFirstName(),
                        schedule.getProvider().getFullName(),
                        schedule.getTime(),
                        TimeUtil.formatChatLabel(schedule.getCreatedAt())
                );
            } else if(schedule.getStatus() == DECLINED) {
                return schedule.getReason();
            } else if(schedule.getStatus() == UNATTENDED) {
                return "This schedule was left unattended. There was no trip action initiated after schedule time clocked";
            } else if(schedule.getStatus() == CANCELLED) {
                return String.format(
                        "%s cancelled %s's scheduled invitation for %s, %s",
                        schedule.getUser().getFirstName(),
                        schedule.getProvider().getFullName(),
                        schedule.getTime(),
                        TimeUtil.formatChatLabel(schedule.getCreatedAt())
                );
            } else if(schedule.getStatus() == ACCEPTED) {
                return String.format(
                        "%s accepted %s's scheduled invitation for %s, %s",
                        schedule.getProvider().getFullName(),
                        schedule.getUser().getFirstName(),
                        schedule.getTime(),
                        TimeUtil.formatChatLabel(schedule.getCreatedAt())
                );
            } else if(schedule.getStatus() == CLOSED) {
                return String.format(
                        "%s closed this scheduled invitation for %s, %s",
                        profileRepository.findById(schedule.getClosedBy()).map(Profile::getFullName).orElse(""),
                        schedule.getTime(),
                        TimeUtil.formatChatLabel(schedule.getCreatedAt())
                );
            } else {
                return String.format(
                        "%s %s %s's schedule invitation for %s, %s",
                        schedule.getProvider().getFullName(),
                        schedule.getStatus().getType(),
                        schedule.getUser().getFirstName(),
                        schedule.getTime(),
                        TimeUtil.formatChatLabel(schedule.getCreatedAt())
                );
            }
        }
    }

    @Override
    public ApiResponse<List<ScheduleGroupResponse>> schedules() {
        List<Schedule> schedules = scheduleRepository.schedules(userUtil.getUser().getId());

        if(schedules != null && !schedules.isEmpty()) {
            List<ScheduleGroupResponse> list = new ArrayList<>();
            Map<LocalDate, List<Schedule>> listMap = schedules.stream()
                    .collect(Collectors.groupingBy(schedule -> schedule.getCreatedAt().toLocalDate()));
            listMap.forEach((date, scheduleList) -> {
                ScheduleGroupResponse response = new ScheduleGroupResponse();
                response.setTime(LocalDateTime.of(date, LocalTime.now()));
                response.setLabel(TimeUtil.formatChatLabel(LocalDateTime.of(date, LocalTime.now())));
                response.setSchedules(scheduleList.stream()
                        .sorted(Comparator.comparing(Schedule::getUpdatedAt))
                        .map(this::response)
                        .toList()
                );
                list.add(response);
            });
            list.sort(Comparator.comparing(ScheduleGroupResponse::getTime));
            return new ApiResponse<>(list);
        }
        return new ApiResponse<>(List.of());
    }

    @Override
    public ApiResponse<List<ScheduleTimeResponse>> times(UUID id) {
        LocalDateTime currentTime = LocalDateTime.now();
        List<Schedule> schedules = getSchedules(id);

        List<ScheduleTimeResponse> list = generateListFromTimes();
        updateCurrentAndPastHours(currentTime, list);
        checkUnavailableTimeInSchedules(schedules, list);
        // Sorting the list based on time (12-hour format, 12:00 to 11:00)
        list.sort((a, b) -> {
            int hourA = Integer.parseInt(a.getTime().split(":")[0]);
            int hourB = Integer.parseInt(b.getTime().split(":")[0]);
            return Integer.compare(hourA == 12 ? 0 : hourA, hourB == 12 ? 0 : hourB);
        });

        return new ApiResponse<>(list);
    }

    private List<ScheduleTimeResponse> generateListFromTimes() {
        List<ScheduleTimeResponse> list = new ArrayList<>();

        // Generating times from 12:00 to 11:00
        for (int i = 0; i < 12; i++) {
            int hour = i == 0 ? 12 : i;  // Handle 12-hour format
            String time = String.format("%d:00", hour);

            ScheduleTimeResponse responseAM = new ScheduleTimeResponse();
            responseAM.setTime(time);
            responseAM.setIsAmTaken(false); // default value
            responseAM.setIsPmTaken(false); // default value
            list.add(responseAM);
        }
        return list;
    }

    private void updateCurrentAndPastHours(LocalDateTime currentTime, List<ScheduleTimeResponse> list) {
        // Marking the current hour and all previous hours as taken
        int currentHour = currentTime.getHour();
        for (int i = 0; i <= currentHour; i++) {
            int hourIndex = i % 12 == 0 ? 0 : i % 12;
            boolean isPm = i >= 12;

            ScheduleTimeResponse response = list.get(hourIndex);
            if (isPm) {
                response.setIsPmTaken(true);
            } else {
                response.setIsAmTaken(true);
            }
        }
    }

    private void checkUnavailableTimeInSchedules(List<Schedule> schedules, List<ScheduleTimeResponse> list) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mma");
        for (Schedule schedule : schedules) {
            LocalTime scheduleTime = LocalTime.parse(schedule.getTime(), formatter);
            int scheduledHour = scheduleTime.getHour() % 12 == 0 ? 12 : scheduleTime.getHour() % 12;
            boolean isScheduledPm = scheduleTime.getHour() >= 12;

            ScheduleTimeResponse response = list.get(scheduledHour % 12 == 0 ? 0 : scheduledHour % 12);
            if (isScheduledPm) {
                response.setIsPmTaken(true);
            } else {
                response.setIsAmTaken(true);
            }
        }
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
        scheduleRepository.findByStatusAndCreatedAtBefore(PENDING, current)
                .forEach(schedule -> {
                    schedule.setStatus(ScheduleStatus.UNATTENDED);
                    schedule.setUpdatedAt(current);
                    schedule.setClosedAt(TimeUtil.formatTimeDifference(getDiff(schedule)));
                    scheduleRepository.save(schedule);
                });

        closePendingOrAcceptedForTheDay(current);
    }

    private void closePendingOrAcceptedForTheDay(LocalDateTime current) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);
        scheduleRepository.findByCreatedAtBetween(startOfDay, endOfDay)
                .stream()
                .filter(schedule -> schedule.getStatus() == PENDING || schedule.getStatus() == ACCEPTED)
                .forEach(schedule -> performCloseAction(current, schedule));
    }

    private void performCloseAction(LocalDateTime current, Schedule schedule) {
        LocalDate today = LocalDate.now();
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mma");
        LocalDateTime requestedTime = LocalDateTime.of(today, LocalTime.parse(schedule.getTime().toUpperCase(), formatter));

        // Check if the requested time has already passed
        if (requestedTime.isBefore(currentTime)) {
            if(schedule.getStatus() == PENDING) {
                schedule.setStatus(DECLINED);
            } else {
                schedule.setStatus(UNATTENDED);
            }
            schedule.setUpdatedAt(current);
            scheduleRepository.save(schedule);
        }
    }
}
