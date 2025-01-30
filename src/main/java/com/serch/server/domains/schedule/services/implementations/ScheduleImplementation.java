package com.serch.server.domains.schedule.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.core.notification.services.NotificationService;
import com.serch.server.enums.schedule.ScheduleStatus;
import com.serch.server.exceptions.others.ScheduleException;
import com.serch.server.exceptions.others.SharedException;
import com.serch.server.mappers.ScheduleMapper;
import com.serch.server.models.account.Profile;
import com.serch.server.models.schedule.Schedule;
import com.serch.server.repositories.account.ProfileRepository;
import com.serch.server.repositories.schedule.ScheduleRepository;
import com.serch.server.domains.conversation.services.ChattingService;
import com.serch.server.domains.schedule.requests.ScheduleDeclineRequest;
import com.serch.server.domains.schedule.requests.ScheduleRequest;
import com.serch.server.domains.schedule.responses.ScheduleResponse;
import com.serch.server.domains.schedule.responses.ScheduleTimeResponse;
import com.serch.server.domains.schedule.services.ScheduleHistoryService;
import com.serch.server.domains.schedule.services.ScheduleService;
import com.serch.server.domains.schedule.services.SchedulingService;
import com.serch.server.domains.transaction.services.SchedulePayService;
import com.serch.server.domains.trip.requests.TripInviteRequest;
import com.serch.server.domains.trip.responses.TripResponse;
import com.serch.server.domains.trip.services.TripService;
import com.serch.server.utils.MoneyUtil;
import com.serch.server.utils.TimeUtil;
import com.serch.server.utils.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.serch.server.enums.schedule.ScheduleStatus.*;

/**
 * Implementation of the ScheduleService interface responsible for managing schedules.
 *
 * @see ScheduleService
 * @see SchedulePayService
 * @see AuthUtil
 * @see ProfileRepository
 * @see ScheduleRepository
 */
@Service
@RequiredArgsConstructor
public class ScheduleImplementation implements ScheduleService {
    private final SchedulePayService payService;
    private final SchedulingService schedulingService;
    private final ScheduleHistoryService historyService;
    private final NotificationService notificationService;
    private final TripService tripService;
    private final ChattingService chattingService;
    private final SimpMessagingTemplate template;
    private final AuthUtil authUtil;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mma");
    private final ProfileRepository profileRepository;
    private final ScheduleRepository scheduleRepository;

    @Value("${application.account.schedule.close.limit}")
    private Integer ACCOUNT_SCHEDULE_CLOSE_LIMIT;
    @Value("${application.account.schedule.close.charge}")
    private Integer ACCOUNT_SCHEDULE_CLOSE_CHARGE;
    @Value("${application.account.schedule.close.duration}")
    private Integer ACCOUNT_SCHEDULE_CLOSE_DURATION;

    private boolean isCurrentUser(UUID id) {
        return authUtil.getUser().getId().equals(id);
    }

    @Override
    @Transactional
    public ApiResponse<ScheduleResponse> schedule(ScheduleRequest request) {
        Profile provider = profileRepository.findById(request.getProvider())
                .orElseThrow(() -> new ScheduleException("Provider not found"));
        Profile user = profileRepository.findById(authUtil.getUser().getId())
                .orElseThrow(() -> new ScheduleException("User not found"));

        validateTime(request);

        // Check if the requested time is already booked
        if (getSchedules(provider.getId()).stream().anyMatch(schedule -> schedule.getTime().equalsIgnoreCase(request.getTime()))) {
            throw new ScheduleException("Provider is already booked for %s today".formatted(request.getTime()));
        } else {
            Schedule schedule = createNewSchedule(request, provider, user);

            sendPendingNotification(schedule);
            chattingService.notifyAboutSchedule(provider.getId());
            notificationService.send(provider.getId(), schedulingService.response(schedule, true, true));

            return new ApiResponse<>(
                    "Schedule placed for %s. ".formatted(request.getTime()) +
                            "%s will be notified".formatted(schedule.getProvider().getFullName()),
                    schedulingService.response(schedule, false, true),
                    HttpStatus.CREATED
            );
        }
    }

    private List<Schedule> getSchedules(UUID provider) {
        LocalDate today = LocalDate.now();

        return scheduleRepository.findByProvider_IdAndCreatedAtBetween(
                provider,
                ZonedDateTime.of(today.atStartOfDay(), TimeUtil.zoneId(authUtil.getUser().getTimezone())),
                ZonedDateTime.of(today.atTime(23, 59, 59), TimeUtil.zoneId(authUtil.getUser().getTimezone()))
        );
    }

    private void validateTime(ScheduleRequest request) {
        LocalDateTime requestedTime = LocalDateTime.of(LocalDate.now(), LocalTime.parse(request.getTime().toUpperCase(), formatter));

        // Check if the requested time has already passed
        if (requestedTime.isBefore(LocalDateTime.now())) {
            throw new ScheduleException("The requested time %s has already passed.".formatted(request.getTime()));
        }
    }

    private Schedule createNewSchedule(ScheduleRequest request, Profile provider, Profile user) {
        Schedule schedule = ScheduleMapper.INSTANCE.schedule(request);
        schedule.setProvider(provider);
        schedule.setUser(user);
        schedule.setStatus(PENDING);
        schedule.setTime(request.getTime().toUpperCase());

        return scheduleRepository.save(schedule);
    }

    @Transactional
    protected void sendActiveNotification(Schedule schedule) {
        template.convertAndSend(
                "/platform/schedule/active/%s".formatted(String.valueOf(schedule.getUser().getId())),
                historyService.active(schedule.getUser().getId())
        );
        template.convertAndSend(
                "/platform/schedule/active/%s".formatted(String.valueOf(schedule.getProvider().getId())),
                historyService.active(schedule.getProvider().getId())
        );

        if(schedule.getProvider().isAssociate()) {
            template.convertAndSend(
                    "/platform/schedule/active/%s".formatted(String.valueOf(schedule.getProvider().getBusiness().getId())),
                    historyService.active(schedule.getProvider().getBusiness().getId())
            );
        }
    }

    @Transactional
    protected void sendPendingNotification(Schedule schedule) {
        template.convertAndSend(
                "/platform/schedule/pending/%s".formatted(String.valueOf(schedule.getUser().getId())),
                historyService.pending(schedule.getUser().getId())
        );
        template.convertAndSend(
                "/platform/schedule/pending/%s".formatted(String.valueOf(schedule.getProvider().getId())),
                historyService.pending(schedule.getProvider().getId())
        );

        if(schedule.getProvider().isAssociate()) {
            template.convertAndSend(
                    "/platform/schedule/pending/%s".formatted(String.valueOf(schedule.getProvider().getBusiness().getId())),
                    historyService.pending(schedule.getProvider().getBusiness().getId())
            );
        }
    }

    @Transactional
    protected void sendInactiveNotification(Schedule schedule) {
        template.convertAndSend(
                "/platform/schedule/history/%s".formatted(String.valueOf(schedule.getUser().getId())),
                historyService.schedules(schedule.getUser().getId())
        );
        template.convertAndSend(
                "/platform/schedule/history/%s".formatted(String.valueOf(schedule.getProvider().getId())),
                historyService.schedules(schedule.getProvider().getId())
        );

        if(schedule.getProvider().isAssociate()) {
            template.convertAndSend(
                    "/platform/schedule/history/%s".formatted(String.valueOf(schedule.getProvider().getBusiness().getId())),
                    historyService.schedules(schedule.getProvider().getBusiness().getId())
            );
        }
    }

    @Override
    @Transactional
    public ApiResponse<String> accept(String id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ScheduleException("Schedule not found"));

        validateTime(ScheduleRequest.builder().time(schedule.getTime()).build());
        if(schedule.getProvider().isSameAs(authUtil.getUser().getId())) {
            if(schedule.getStatus() == PENDING) {
                schedule.setStatus(ACCEPTED);
                schedule.setUpdatedAt(TimeUtil.now());
                scheduleRepository.save(schedule);

                sendActiveNotification(schedule);
                sendPendingNotification(schedule);
                chattingService.notifyAboutSchedule(schedule.getUser().getId());
                notificationService.send(schedule.getUser().getId(), schedulingService.response(schedule, false, true));

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
    @Transactional
    public ApiResponse<String> cancel(String id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ScheduleException("Schedule not found"));

        if(schedule.getUser().isSameAs(authUtil.getUser().getId())) {
            if(schedule.getStatus() == PENDING) {
                schedule.setStatus(CANCELLED);
                schedule.setUpdatedAt(TimeUtil.now());
                scheduleRepository.save(schedule);

                sendInactiveNotification(schedule);
                sendPendingNotification(schedule);
                chattingService.notifyAboutSchedule(schedule.getProvider().getId());
                notificationService.send(schedule.getProvider().getId(), schedulingService.response(schedule, true, true));

                return new ApiResponse<>(
                        "Schedule cancelled. %s will not be notified".formatted(schedule.getProvider().getFullName()),
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
    @Transactional
    public ApiResponse<String> close(String id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ScheduleException("Schedule not found"));

        if(isCurrentUser(schedule.getUser().getId()) || isCurrentUser(schedule.getProvider().getId())) {
            if(schedule.getStatus() == ACCEPTED) {
                long diff = getDiff(schedule);

                schedule.setStatus(ScheduleStatus.CLOSED);
                schedule.setUpdatedAt(TimeUtil.now());
                schedule.setClosedAt(TimeUtil.formatTimeDifference(diff));
                schedule.setClosedBy(authUtil.getUser().getId());
                schedule.setClosedOnTime(diff <= ACCOUNT_SCHEDULE_CLOSE_DURATION && diff >= 0);
                scheduleRepository.save(schedule);

                sendInactiveNotification(schedule);
                sendActiveNotification(schedule);
                notificationService.send(
                        isCurrentUser(schedule.getProvider().getId()) ? schedule.getUser().getId() : schedule.getProvider().getId(),
                        schedulingService.response(schedule, isCurrentUser(schedule.getProvider().getId()), true)
                );

                int closed = scheduleRepository.findByClosedByAndClosedOnTime(authUtil.getUser().getId(), false).size();
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
        return ChronoUnit.MINUTES.between(LocalTime.now(), LocalTime.parse(schedule.getTime(), formatter));
    }

    @Override
    @Transactional
    public ApiResponse<TripResponse> start(String id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ScheduleException("Schedule not found"));

        if(schedule.getProvider().isSameAs(authUtil.getUser().getId()) || schedule.getUser().isSameAs(authUtil.getUser().getId())) {
            if(schedule.getStatus() == ACCEPTED) {
                if(schedule.getProvider() == null) {
                    throw new SharedException("Provider not found");
                } else if(schedule.getProvider().getActive() == null) {
                    throw new SharedException("Provider not active");
                }

                schedule.setClosedAt(TimeUtil.formatTimeDifference(getDiff(schedule)));
                schedule.setStatus(ScheduleStatus.ATTENDED);
                scheduleRepository.save(schedule);

                TripInviteRequest request = ScheduleMapper.INSTANCE.request(schedule);
                request.setCategory(schedule.getProvider().getCategory());

                sendActiveNotification(schedule);
                sendInactiveNotification(schedule);

                return tripService.request(request, schedule.getUser(), schedule.getProvider());
            } else {
                throw new ScheduleException("Cannot start a trip with unaccepted schedule");
            }
        } else {
            throw new ScheduleException("An error occurred while trying to start a scheduled trip");
        }
    }

    @Override
    @Transactional
    public ApiResponse<String> decline(ScheduleDeclineRequest request) {
        Schedule schedule = scheduleRepository.findById(request.getId())
                .orElseThrow(() -> new ScheduleException("Schedule not found"));

        if(request.getReason() == null || request.getReason().isEmpty()) {
            throw new ScheduleException("Reason for declining is required");
        } else {
            if(schedule.getProvider().isSameAs(authUtil.getUser().getId())) {
                if(schedule.getStatus() == PENDING) {
                    schedule.setStatus(ScheduleStatus.DECLINED);
                    schedule.setUpdatedAt(TimeUtil.now());
                    scheduleRepository.save(schedule);

                    sendInactiveNotification(schedule);
                    sendPendingNotification(schedule);
                    chattingService.notifyAboutSchedule(schedule.getUser().getId());
                    notificationService.send(schedule.getUser().getId(), schedulingService.response(schedule, false, true));

                    return new ApiResponse<>(
                            "Schedule declined. %s will not be notified".formatted(schedule.getUser().getFullName()),
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
    public ApiResponse<List<ScheduleTimeResponse>> times(UUID id) {
        List<ScheduleTimeResponse> list = generateListFromTimes();
        updateCurrentAndPastHours(LocalDateTime.now(), list);
        checkUnavailableTimeInSchedules(getSchedules(id), list);
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
            response.setIsPmTaken(isPm);
            response.setIsAmTaken(!isPm);
        }
    }

    private void checkUnavailableTimeInSchedules(List<Schedule> schedules, List<ScheduleTimeResponse> list) {
        for (Schedule schedule : schedules) {
            LocalTime scheduleTime = LocalTime.parse(schedule.getTime().trim(), formatter);
            int scheduledHour = scheduleTime.getHour() % 12 == 0 ? 12 : scheduleTime.getHour() % 12;
            boolean isScheduledPm = scheduleTime.getHour() >= 12;

            ScheduleTimeResponse response = list.get(scheduledHour % 12 == 0 ? 0 : scheduledHour % 12);
            response.setIsPmTaken(isScheduledPm);
            response.setIsAmTaken(!isScheduledPm);
        }
    }

    @Override
    @Transactional
    public void notifySchedules() {
        LocalDate today = LocalDate.now();
        scheduleRepository.findByCreatedAtBetween(ZonedDateTime.of(today.atStartOfDay(), ZoneOffset.UTC), ZonedDateTime.of(today.atTime(23, 59, 59), ZoneOffset.UTC))
                .forEach(schedule -> {
                    LocalTime time = LocalTime.parse(schedule.getTime(), DateTimeFormatter.ofPattern("h:mma"));

                    if(time.equals(LocalTime.now())) {
                        notificationService.send(
                                schedule.getProvider().getId(),
                                schedulingService.response(schedule, true, true)
                        );
                        notificationService.send(
                                schedule.getUser().getId(),
                                schedulingService.response(schedule, false, true)
                        );
                    }
                });
    }

    @Override
    @Transactional
    public void closePastUnaccepted() {
        ZonedDateTime current = TimeUtil.now();

        scheduleRepository.findByStatusAndCreatedAtBefore(PENDING, current)
                .forEach(schedule -> {
                    schedule.setStatus(ScheduleStatus.UNATTENDED);
                    schedule.setUpdatedAt(current);
                    schedule.setClosedAt(TimeUtil.formatTimeDifference(getDiff(schedule)));
                    scheduleRepository.save(schedule);
                });

        closePendingOrAcceptedForTheDay(current);
    }

    @Transactional
    protected void closePendingOrAcceptedForTheDay(ZonedDateTime current) {
        scheduleRepository.findByCreatedAtBetween(ZonedDateTime.of(LocalDate.now().atStartOfDay(), ZoneOffset.UTC), ZonedDateTime.of(LocalDate.now().atStartOfDay(), ZoneOffset.UTC))
                .stream()
                .filter(schedule -> schedule.getStatus() == PENDING || schedule.getStatus() == ACCEPTED)
                .forEach(schedule -> performCloseAction(current, schedule));
    }

    @Transactional
    protected void performCloseAction(ZonedDateTime current, Schedule schedule) {
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime requestedTime = LocalDateTime.of(LocalDate.now(), LocalTime.parse(schedule.getTime().toUpperCase(), formatter));

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

        sendActiveNotification(schedule);
        sendPendingNotification(schedule);
        sendInactiveNotification(schedule);
    }
}
