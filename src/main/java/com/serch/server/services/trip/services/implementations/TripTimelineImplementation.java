package com.serch.server.services.trip.services.implementations;

import com.serch.server.enums.trip.TripConnectionStatus;
import com.serch.server.models.trip.Trip;
import com.serch.server.models.trip.TripShare;
import com.serch.server.models.trip.TripTimeline;
import com.serch.server.repositories.trip.TripTimelineRepository;
import com.serch.server.services.trip.responses.TripTimelineResponse;
import com.serch.server.services.trip.services.TripTimelineService;
import com.serch.server.utils.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TripTimelineImplementation implements TripTimelineService {
    private final TripTimelineRepository tripTimelineRepository;

    @Override
    @Transactional(propagation = Propagation.NESTED)
    public void create(Trip trip, TripShare share, TripConnectionStatus status) {
        if(share != null) {
            tripTimelineRepository.findByStatusAndSharing_Id(status, share.getId())
                    .ifPresentOrElse(timeline -> {
                        timeline.setUpdatedAt(TimeUtil.now());
                        timeline.setSharing(share);
                        tripTimelineRepository.save(timeline);
                    }, () -> {
                        TripTimeline timeline = new TripTimeline();
                        timeline.setStatus(status);
                        timeline.setSharing(share);
                        tripTimelineRepository.save(timeline);
                    });
        } else {
            tripTimelineRepository.findByStatusAndTrip_Id(status, trip.getId())
                    .ifPresentOrElse(timeline -> {
                        timeline.setUpdatedAt(TimeUtil.now());
                        timeline.setTrip(trip);
                        tripTimelineRepository.save(timeline);
                    }, () -> {
                        TripTimeline timeline = new TripTimeline();
                        timeline.setStatus(status);
                        timeline.setTrip(trip);
                        tripTimelineRepository.save(timeline);
                    });
        }
    }

    @Override
    @Transactional(propagation = Propagation.NESTED)
    public TripTimelineResponse response(TripTimeline timeline) {
        return null;
    }

    @Override
    @Transactional(propagation = Propagation.NESTED)
    public List<TripTimelineResponse> response(Trip trip) {
        return List.of();
    }
}
