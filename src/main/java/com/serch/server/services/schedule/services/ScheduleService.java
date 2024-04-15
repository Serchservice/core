package com.serch.server.services.schedule.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.schedule.requests.ScheduleDeclineRequest;
import com.serch.server.services.schedule.requests.ScheduleRequest;
import com.serch.server.services.schedule.responses.ScheduleResponse;
import com.serch.server.services.schedule.responses.ScheduleTimeResponse;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing schedules, including scheduling, acceptance, cancellation,
 * closure, starting, declining, and retrieving schedule information.
 *
 * @see ScheduleImplementation
 */
public interface ScheduleService {

    /**
     * Places a schedule request based on the provided details.
     * Checks for existing schedules for the provider on the current day
     * and ensures the provider is not already booked for the requested time.
     *
     * @param request The schedule request containing necessary information.
     * @return A response indicating the status of the scheduling operation.
     *
     * @see ScheduleRequest
     */
    ApiResponse<String> request(ScheduleRequest request);

    /**
     * Accepts a scheduled appointment identified by its ID.
     * Only the provider associated with the schedule can accept it.
     *
     * @param id The ID of the schedule to be accepted.
     * @return A response indicating the status of the acceptance operation.
     */
    ApiResponse<String> accept(String id);

    /**
     * Cancels a scheduled appointment identified by its ID.
     * Only the user who scheduled the appointment can cancel it.
     *
     * @param id The ID of the schedule to be canceled.
     * @return A response indicating the status of the cancellation operation.
     */
    ApiResponse<String> cancel(String id);

    /**
     * Closes a scheduled appointment identified by its ID.
     * The closure involves marking the schedule as closed and potentially charging
     * the user based on certain conditions.
     *
     * @param id The ID of the schedule to be closed.
     * @return A response indicating the status of the closure operation.
     */
    ApiResponse<String> close(String id);

    /**
     * Marks a scheduled appointment as started.
     * This method is intended for future use and is not yet implemented.
     *
     * @param id The ID of the schedule to be started.
     * @return A response indicating the status of the operation.
     */
    ApiResponse<String> start(String id);

    /**
     * Declines a scheduled appointment based on the provided details.
     * Only the provider associated with the schedule can decline it.
     *
     * @param request The decline request containing the schedule ID and reason for declining.
     * @return A response indicating the status of the decline operation.
     *
     * @see ScheduleDeclineRequest
     */
    ApiResponse<String> decline(ScheduleDeclineRequest request);

    /**
     * Retrieves a list of scheduled appointments for the current day.
     *
     * @return A response containing a list of scheduled appointments for the current day.
     *
     * @see ScheduleResponse
     */
    ApiResponse<List<ScheduleResponse>> today();

    /**
     * Retrieves a list of all scheduled appointments for the user.
     *
     * @return A response containing a list of all scheduled appointments for the user.
     *
     * @see ScheduleResponse
     */
    ApiResponse<List<ScheduleResponse>> schedules();

    /**
     * Retrieves available time slots for scheduling appointments with a specific provider.
     *
     * @param id The ID of the provider for whom time slots are to be retrieved.
     * @return A response containing a list of available time slots.
     *
     * @see ScheduleTimeResponse
     */
    ApiResponse<List<ScheduleTimeResponse>> times(UUID id);

    /**
     * Checks for schedules that have reached its scheduled time and send notification
     * to the device.
     */
    void notifySchedules();
    /**
     * Checks for schedules that have passed the day it was created and closes it if it was not
     * accepted or attended to.
     */
    void closePastUnaccepted();
}