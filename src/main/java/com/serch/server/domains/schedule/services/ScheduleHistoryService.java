package com.serch.server.domains.schedule.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.domains.schedule.responses.ScheduleGroupResponse;
import com.serch.server.domains.schedule.responses.ScheduleResponse;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public interface ScheduleHistoryService {
    /**
     * Fetch the list of schedule list for a user
     *
     * @param id The user id to fetch the schedules for
     * @return A list of {@link ScheduleResponse}
     */
    List<ScheduleResponse> active(UUID id);

    /**
     * Fetch the list of pending schedule list for a user
     *
     * @param id The user id to fetch the schedules for
     * @return A list of {@link ScheduleResponse}
     */
    List<ScheduleResponse> pending(UUID id);

    /**
     * Fetch the list of schedule grouped list for a user
     *
     * @param id The user id to fetch the schedules for
     * @return A list of {@link ScheduleGroupResponse}
     */
    List<ScheduleGroupResponse> schedules(UUID id);

    /**
     * Retrieves a list of scheduled appointments for the current day.
     *
     * @return A response containing a list of scheduled appointments for the current day.
     *
     * @param page The page number to retrieve (zero-based index).
     * @param size The number of items per page.
     * @see ScheduleResponse
     */
    ApiResponse<List<ScheduleResponse>> active(Integer page, Integer size);

    /**
     * Retrieves a list of requested scheduled appointments for the current day.
     *
     * @return A response containing a list of scheduled appointments for the current day.
     *
     * @param page The page number to retrieve (zero-based index).
     * @param size The number of items per page.
     * @see ScheduleResponse
     */
    ApiResponse<List<ScheduleResponse>> requested(Integer page, Integer size);

    /**
     * Retrieves a list of all scheduled appointments for the user.
     *
     * @return A response containing a list of all scheduled appointments for the user.
     *
     * @param dateTime   The date to fetch its history.
     * @param category   The category to fetch the history for.
     * @param status   The schedule status to look for.
     * @param page The page number to retrieve (zero-based index).
     * @param size The number of items per page.
     * @see ScheduleGroupResponse
     */
    ApiResponse<List<ScheduleGroupResponse>> history(Integer page, Integer size, String status, String category, ZonedDateTime dateTime);
}
