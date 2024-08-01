package com.serch.server.services.trip.services;

import com.serch.server.enums.trip.TripConnectionStatus;
import com.serch.server.models.trip.Trip;
import com.serch.server.models.trip.TripShare;
import com.serch.server.models.trip.TripTimeline;
import com.serch.server.services.trip.responses.TripTimelineResponse;

import java.util.List;

public interface TripTimelineService {
    void create(Trip trip, TripShare share, TripConnectionStatus status);
    TripTimelineResponse response(TripTimeline timeline);
    List<TripTimelineResponse> response(Trip trip);
}
