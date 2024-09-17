package com.serch.server.services.schedule.services;

import com.serch.server.mappers.ScheduleMapper;
import com.serch.server.models.account.Profile;
import com.serch.server.models.schedule.Schedule;
import com.serch.server.repositories.account.ProfileRepository;
import com.serch.server.services.schedule.responses.ScheduleResponse;
import com.serch.server.utils.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static com.serch.server.enums.schedule.ScheduleStatus.*;

@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.NESTED)
public class SchedulingImplementation implements SchedulingService {
    private final ProfileRepository profileRepository;

    @Override
    public ScheduleResponse response(Schedule schedule, boolean isProvider, boolean isNotBusiness) {
        Profile profile = isProvider ? schedule.getUser() : schedule.getProvider();

        ScheduleResponse response = ScheduleMapper.INSTANCE.response(schedule);
        response.setName(profile.getFullName());
        response.setAvatar(profile.getAvatar());
        response.setCategory(profile.getCategory().getType());
        response.setImage(profile.getCategory().getImage());
        response.setRating(profile.getRating());
        response.setLabel(TimeUtil.formatDay(schedule.getCreatedAt(), profile.getUser().getTimezone()));
        response.setReason(buildReason(schedule, isProvider, isNotBusiness));

        if(schedule.getClosedBy() != null) {
            response.setClosedBy(profileRepository.findById(schedule.getClosedBy()).map(Profile::getFullName).orElse(""));
        } else {
            response.setClosedBy("From Serch");
        }
        return response;
    }

    private String buildReason(Schedule schedule, boolean isProvider, boolean isNotBusiness) {
        if(isNotBusiness) {
            if(isProvider) {
                if(schedule.getStatus() == PENDING) {
                    return String.format(
                            "%s invited you for a scheduled service trip for %s, %s",
                            schedule.getUser().getUser().getFirstName(),
                            schedule.getTime(),
                            TimeUtil.formatChatLabel(schedule.getCreatedAt().toLocalDateTime(), "")
                    );
                } else if(schedule.getStatus() == DECLINED) {
                    if(schedule.getReason() != null && !schedule.getReason().isEmpty()) {
                        return String.format(
                                "You declined %s's schedule request and has this to say: \n\n%s",
                                schedule.getUser().getUser().getFirstName(),
                                schedule.getReason()
                        );
                    } else {
                        return String.format(
                                "You declined %s's schedule request",
                                schedule.getUser().getUser().getFirstName()
                        );
                    }
                } else if(schedule.getStatus() == UNATTENDED) {
                    return "This schedule was left unattended. There was no trip action initiated after schedule time clocked";
                } else if(schedule.getStatus() == CANCELLED) {
                    return String.format(
                            "%s cancelled your scheduled invitation for %s, %s",
                            schedule.getUser().getUser().getFirstName(),
                            schedule.getTime(),
                            TimeUtil.formatChatLabel(schedule.getCreatedAt().toLocalDateTime(), "")
                    );
                } else if(schedule.getStatus() == ACCEPTED) {
                    return String.format(
                            "You accepted %s's scheduled invitation for %s, %s",
                            schedule.getUser().getUser().getFirstName(),
                            schedule.getTime(),
                            TimeUtil.formatChatLabel(schedule.getCreatedAt().toLocalDateTime(), "")
                    );
                } else if(schedule.getStatus() == CLOSED) {
                    return String.format(
                            "%s closed this scheduled invitation for %s, %s",
                            profileRepository.findById(schedule.getClosedBy()).map(Profile::getFullName).orElse(""),
                            schedule.getTime(),
                            TimeUtil.formatChatLabel(schedule.getCreatedAt().toLocalDateTime(), "")
                    );
                } else {
                    return String.format(
                            "You %s %s's schedule invitation for %s, %s",
                            schedule.getStatus().getType(),
                            schedule.getUser().getUser().getFirstName(),
                            schedule.getTime(),
                            TimeUtil.formatChatLabel(schedule.getCreatedAt().toLocalDateTime(), "")
                    );
                }
            } else {
                if(schedule.getStatus() == PENDING) {
                    return String.format(
                            "You invited %s for a scheduled service trip for %s, %s",
                            schedule.getProvider().getFullName(),
                            schedule.getTime(),
                            TimeUtil.formatChatLabel(schedule.getCreatedAt().toLocalDateTime(), "")
                    );
                } else if(schedule.getStatus() == DECLINED) {
                    if(schedule.getReason() != null && !schedule.getReason().isEmpty()) {
                        return String.format(
                                "%s declined your schedule request and has this to say: \n\n%s",
                                schedule.getProvider().getFullName(),
                                schedule.getReason()
                        );
                    } else {
                        return String.format(
                                "%s declined your schedule request",
                                schedule.getProvider().getFullName()
                        );
                    }
                } else if(schedule.getStatus() == UNATTENDED) {
                    return "This schedule was left unattended. There was no trip action initiated after schedule time clocked";
                } else if(schedule.getStatus() == CANCELLED) {
                    return String.format(
                            "You cancelled %s's scheduled invitation for %s, %s",
                            schedule.getProvider().getFullName(),
                            schedule.getTime(),
                            TimeUtil.formatChatLabel(schedule.getCreatedAt().toLocalDateTime(), "")
                    );
                } else if(schedule.getStatus() == ACCEPTED) {
                    return String.format(
                            "%s accepted your scheduled invitation for %s, %s",
                            schedule.getProvider().getFullName(),
                            schedule.getTime(),
                            TimeUtil.formatChatLabel(schedule.getCreatedAt().toLocalDateTime(), "")
                    );
                } else {
                    return buildScheduleReason(schedule);
                }
            }
        } else {
            if(schedule.getStatus() == PENDING) {
                return String.format(
                        "%s invited %s for a scheduled service trip for %s, %s",
                        schedule.getUser().getUser().getFirstName(),
                        schedule.getProvider().getFullName(),
                        schedule.getTime(),
                        TimeUtil.formatChatLabel(schedule.getCreatedAt().toLocalDateTime(), "")
                );
            } else if(schedule.getStatus() == DECLINED) {
                if(schedule.getReason() != null && !schedule.getReason().isEmpty()) {
                    return String.format(
                            "%s declined %s's schedule request and has this to say: \n\n%s",
                            schedule.getProvider().getFullName(),
                            schedule.getUser().getUser().getFirstName(),
                            schedule.getReason()
                    );
                } else {
                    return String.format(
                            "%s declined %s's schedule request",
                            schedule.getProvider().getFullName(),
                            schedule.getUser().getUser().getFirstName()
                    );
                }
            } else if(schedule.getStatus() == UNATTENDED) {
                return "This schedule was left unattended. There was no trip action initiated after schedule time clocked";
            } else if(schedule.getStatus() == CANCELLED) {
                return String.format(
                        "%s cancelled %s's scheduled invitation for %s, %s",
                        schedule.getUser().getUser().getFirstName(),
                        schedule.getProvider().getFullName(),
                        schedule.getTime(),
                        TimeUtil.formatChatLabel(schedule.getCreatedAt().toLocalDateTime(), "")
                );
            } else if(schedule.getStatus() == ACCEPTED) {
                return String.format(
                        "%s accepted %s's scheduled invitation for %s, %s",
                        schedule.getProvider().getFullName(),
                        schedule.getUser().getUser().getFirstName(),
                        schedule.getTime(),
                        TimeUtil.formatChatLabel(schedule.getCreatedAt().toLocalDateTime(), "")
                );
            } else return buildScheduleReason(schedule);
        }
    }

    private String buildScheduleReason(Schedule schedule) {
        if(schedule.getStatus() == CLOSED) {
            return String.format(
                    "%s closed this scheduled invitation for %s, %s",
                    profileRepository.findById(schedule.getClosedBy()).map(Profile::getFullName).orElse(""),
                    schedule.getTime(),
                    TimeUtil.formatChatLabel(schedule.getCreatedAt().toLocalDateTime(), "")
            );
        } else {
            return String.format(
                    "%s %s %s's schedule invitation for %s, %s",
                    schedule.getProvider().getFullName(),
                    schedule.getStatus().getType(),
                    schedule.getUser().getUser().getFirstName(),
                    schedule.getTime(),
                    TimeUtil.formatChatLabel(schedule.getCreatedAt().toLocalDateTime(), "")
            );
        }
    }
}
