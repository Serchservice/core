package com.serch.server.domains.schedule.services;

import com.serch.server.models.schedule.Schedule;
import com.serch.server.domains.schedule.responses.ScheduleResponse;

public interface SchedulingService {
    /**
     * Prepares the schedule response data that is readable to the user
     *
     * @param schedule The {@link Schedule} data to prepare the response from
     * @param isProvider Checks if data prepared is for a provider
     * @param isNotBusiness Checks if the data prepared is not for a business account
     *
     * @return {@link ScheduleResponse} data
     */
    ScheduleResponse response(Schedule schedule, boolean isProvider, boolean isNotBusiness);

//    /**
//     * Prepares the schedule response data that is readable to the user
//     *
//     * @param schedule The {@link Schedule} data to prepare the response from
//     * @param id The id of the user whose
//     *
//     * @return {@link ScheduleResponse} data
//     */
//    ScheduleResponse response(Schedule schedule, UUID id);
}
