package com.serch.server.services.transaction.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.models.schedule.Schedule;

/**
 * This is a wrapper class for the implementation of Schedule payment.
 * @see com.serch.server.services.transaction.services.implementations.SchedulePayImplementation
 */
public interface SchedulePayService {
    /**
     * Charges the user for closing a schedule.
     * @param schedule The schedule to be closed.
     * @return ApiResponse indicating the success or failure of the charge operation.
     */
    ApiResponse<Boolean> charge(Schedule schedule);
    /**
     * Processes scheduled payments.
     */
    void pay();
}
