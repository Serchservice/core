package com.serch.server.domains.nearby.utils;

import com.serch.server.domains.nearby.models.go.activity.GoActivity;
import com.serch.server.domains.nearby.services.activity.requests.GoCreateActivityRequest;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Utility class for validating activity time constraints.
 * <p>
 * This class ensures that activities have valid start and end times,
 * preventing invalid timelines such as past events or overlapping constraints.
 * </p>
 */
public class GoValidator {
    private Boolean _canContinue = false;
    private String _error = "";

    public Boolean getCanContinue() {
        return _canContinue;
    }

    public String getError() {
        return _error;
    }

    /**
     * Chains multiple validations together for a given activity.
     *
     * @param activity The {@link GoActivity} instance containing start and end times.
     * @param request  The {@link GoCreateActivityRequest} containing the activity date.
     * @return The final {@link GoValidator} instance after validation.
     */
    public static GoValidator chain(GoActivity activity, GoCreateActivityRequest request) {
        return new GoValidator()
                .enforceStarter(activity.getStartTime())
                .validateActivityTimeline(activity.getStartTime(), activity.getEndTime())
                .validateActivityStartTime(activity.getStartTime(), request.getDate())
                .validateActivityEndTime(activity.getEndTime(), request.getDate());
    }

    /**
     * Ensures that the provided start time is valid.
     *
     * @param start The activity start time.
     * @return The current {@link GoValidator} instance if valid.
     */
    private GoValidator enforceStarter(LocalTime start) {
        if (start.getHour() == 0) {
            _canContinue = false;
            _error = "You cannot create an activity that starts at 12 AM.";
        } else {
            _canContinue = true;
            _error = "";
        }
        return this;
    }

    /**
     * Ensures the provided `start` time is before the `end` time.
     *
     * @param start The activity start time.
     * @param end   The activity end time.
     * @return The current {@link GoValidator} instance if valid.
     */
    public GoValidator validateActivityTimeline(LocalTime start, LocalTime end) {
        if (!_canContinue) return this;

        if (start.isBefore(end)) {
            _canContinue = true;
            _error = "";
        } else {
            _canContinue = false;
            _error = "Activity start time must be before the end time.";
        }
        return this;
    }

    /**
     * Validates whether an activity's start time is in the present or future.
     *
     * @param time The activity start time.
     * @param date The activity start date.
     * @return The current {@link GoValidator} instance if valid.
     */
    public GoValidator validateActivityStartTime(LocalTime time, LocalDate date) {
        if (!_canContinue) return this;

        if (validate(time, date)) {
            _canContinue = true;
            _error = "";
        } else {
            _canContinue = false;
            _error = "Activity start date and time must be in the present or future.";
        }
        return this;
    }

    /**
     * Validates whether an activity's end time is in the present or future.
     *
     * @param time The activity end time.
     * @param date The activity end date.
     * @return The current {@link GoValidator} instance if valid.
     */
    public GoValidator validateActivityEndTime(LocalTime time, LocalDate date) {
        if (!_canContinue) return this;

        if(validate(time, date)) {
            _canContinue = true;
            _error = "";
        }  else {
            _canContinue = false;
            _error = "Activity ending time must be in the future or present";
        }

        return this;
    }

    /**
     * Checks whether the provided `time` and `date` are today or in the future.
     * <p>
     * An activity is considered valid if it is scheduled for today or a future date.
     * If the activity is scheduled for today, the time must not be in the past.
     * </p>
     *
     * @param time The activity time.
     * @param date The activity date.
     * @return `true` if the activity timeline is valid, `false` otherwise.
     */
    private boolean validate(LocalTime time, LocalDate date) {
        LocalTime now = LocalTime.now();
        LocalDate nowDate = LocalDate.now();

        if (nowDate.equals(date)) {
            return !time.isBefore(now);
        } else if (nowDate.isBefore(date)) {
            return true;
        } else {
            return date.equals(nowDate);
        }
    }
}
