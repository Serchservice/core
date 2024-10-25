package com.serch.server.services.trip.services;

import com.serch.server.enums.trip.TripConnectionStatus;
import com.serch.server.models.trip.Trip;
import com.serch.server.models.trip.TripShare;
import com.serch.server.models.trip.TripTimeline;
import com.serch.server.services.trip.responses.TripTimelineResponse;

import java.util.List;

/**
 * TripTimelineService interface defines the operations related to managing
 * and processing trip timelines. This includes creating timeline entries for
 * trips, generating responses based on timeline data, and retrieving timelines
 * for specific trips.
 */
public interface TripTimelineService {

    /**
     * Creates a new entry in the trip timeline.
     *
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

    /**
     * Generates a response object from a given trip timeline entry.
     *
     * This method processes a specific {@link TripTimeline} and converts it
     * into a {@link TripTimelineResponse}, which is typically used for returning
     * to the client in a structured format. The response may contain relevant
     * information such as timestamps, descriptions of events, and the current
     * status of the trip.
     *
     * @param timeline The {@link TripTimeline} object containing details of the
     *                 trip timeline entry to be processed. This entry may include
     *                 information about specific events, changes, or milestones
     *                 related to the trip.
     *
     * @return A {@link TripTimelineResponse} containing formatted data based
     *         on the provided timeline, ready for client consumption.
     */
    TripTimelineResponse response(TripTimeline timeline);

    /**
     * Retrieves a list of responses for all trip timeline entries associated
     * with a specific trip.
     *
     * This method takes a {@link Trip} object and gathers all corresponding
     * timeline entries, converting them into a list of {@link TripTimelineResponse}
     * objects. This is useful for displaying the full history of a trip, including
     * important events and their outcomes.
     *
     * @param trip The {@link Trip} object for which to retrieve the timeline entries.
     *             This object should contain the trip identifier, which is used to
     *             fetch the relevant timeline entries from the data source.
     *
     * @return A list of {@link TripTimelineResponse} objects that detail the timeline
     *         for the specified trip, allowing users to see the sequence of events
     *         throughout the trip's history.
     */
    List<TripTimelineResponse> response(Trip trip);
}