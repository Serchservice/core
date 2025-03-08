package com.serch.server.domains.nearby.services.activity.services;

import com.serch.server.domains.nearby.models.go.activity.GoActivity;

/**
 * This interface defines callbacks for handling different lifecycle events of a GoActivity.
 * <p></p>
 * Implementations of this interface can be used to trigger specific actions
 * or perform side effects based on the current state of a GoActivity.
 * <p></p>
 * For example, sending notifications to users when an activity is created,
 * updating related data in other systems when an activity is started or ended,
 * or performing cleanup tasks when an activity is deleted.
 */
public interface GoActivityLifecycleService {
    /**
     * Callback method invoked when a GoActivity is created.
     *
     * @param activity The newly created {@link GoActivity} object.
     */
    void onCreated(GoActivity activity);

    /**
     * Callback method invoked when a user attends a GoActivity.
     *
     * @param activity The {@link GoActivity} that the user is attending.
     */
    void onAttending(GoActivity activity);

    /**
     * Callback method invoked when a GoActivity is started.
     *
     * @param activity The {@link GoActivity} that has been started.
     */
    void onStarted(GoActivity activity);

    /**
     * Callback method invoked when a GoActivity is ended.
     *
     * @param activity The {@link GoActivity} that has been ended.
     */
    void onEnded(GoActivity activity);

    /**
     * Callback method invoked when a GoActivity is deleted.
     *
     * @param activity The {@link GoActivity} that has been deleted.
     */
    void onDeleted(GoActivity activity);
}