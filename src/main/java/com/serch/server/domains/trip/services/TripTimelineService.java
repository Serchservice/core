package com.serch.server.domains.trip.services;

import com.serch.server.enums.trip.TripConnectionStatus;
import com.serch.server.models.trip.Trip;
import com.serch.server.models.trip.TripShare;

/**
 * TripTimelineService interface defines the operations related to managing
 * and processing trip timelines. This includes creating timeline entries for
 * trips, generating responses based on timeline data, and retrieving timelines
 * for specific trips.
 */
public interface TripTimelineService {

    /**
     * Creates a new entry in the trip timeline.
     * <p>
     * This method records the progress or significant events related to a specific trip.
     * It associates a trip with its share status and current connection status, enabling
     * the tracking of events over the trip's lifecycle.
     *
     * @param trip The {@link Trip} object representing the trip for which the timeline
     *             entry is being created. This object contains details such as
     *             destination, start and end times, and participant information.
     * @param share The {@link TripShare} object that indicates whether the trip
     *              details are being shared with other users or stakeholders.
     * @param status The current status of the trip connection represented by
     *               {@link TripConnectionStatus}. This status indicates the
     *               connectivity or relationship of the trip to other trips or
     *               entities, such as active, completed, or canceled.
     */
    void create(Trip trip, TripShare share, TripConnectionStatus status);
}