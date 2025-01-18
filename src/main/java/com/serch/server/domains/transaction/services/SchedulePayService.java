package com.serch.server.domains.transaction.services;

import com.serch.server.models.schedule.Schedule;

/**
 * SchedulePayService interface defines operations related to processing scheduled payments
 * and charging users for closing schedules.
 *<p>
 * This service is responsible for handling payment operations associated with schedules,
 * including charging users when a schedule is closed and processing any scheduled payments
 * that are due.
 * </p>
 *
 * @see com.serch.server.domains.transaction.services.implementations.SchedulePayImplementation
 */
public interface SchedulePayService {

    /**
     * Charges the user for closing a specified schedule.
     *<p>
     * This method processes a payment for the provided schedule, which indicates that the
     * user intends to finalize or close the schedule. If the payment is successful, it returns
     * true; otherwise, it returns false, indicating a failure in charging the user.
     *</p>
     * @param schedule The schedule to be closed. This object contains all relevant details
     *                 about the schedule, including associated costs and user information.
     *
     * @return boolean indicating the success (true) or failure (false) of the charge operation.
     *         A successful charge implies that the user's payment has been processed,
     *         while a failure indicates that the charge was not completed.
     */
    boolean charge(Schedule schedule);

    /**
     * Processes scheduled payments for users.
     * <p>
     * This method handles the execution of any payments that are scheduled to occur.
     * It checks for any due payments, processes them accordingly, and may update the
     * status of the related schedules. This operation is typically performed at regular
     * intervals to ensure timely payment processing.
     * </p>
     */
    void processPayments();
}