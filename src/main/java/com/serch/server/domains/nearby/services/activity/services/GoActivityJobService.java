package com.serch.server.domains.nearby.services.activity.services;

/**
 * This interface defines the contract for processing activity-related jobs.
 * <p></p>
 * Implementations of this interface are responsible for executing
 * background tasks related to activities, such as sending notifications,
 * updating activity statuses, or performing other activity-related operations.
 */
public interface GoActivityJobService {
    /**
     * Executes the activity processing job.
     */
    void process();

    /**
     * Handles a processor to check for activity updates at the end of each day.
     */
    void handle();

    /**
     * Handles a processor to check for activity updates for past activities.
     */
    void run();
}