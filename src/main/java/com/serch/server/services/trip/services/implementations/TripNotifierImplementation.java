package com.serch.server.services.trip.services.implementations;

import com.serch.server.services.trip.services.TripNotifier;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TripNotifierImplementation implements TripNotifier {
    @Value("${application.web.socket.topic.trip}")
    private String TRIP_TOPIC;
}
