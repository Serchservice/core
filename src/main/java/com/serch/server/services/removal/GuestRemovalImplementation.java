package com.serch.server.services.removal;

import com.serch.server.models.shared.Guest;
import com.serch.server.repositories.shared.GuestRepository;
import com.serch.server.repositories.shared.SharedLinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GuestRemovalImplementation implements GuestRemovalService {
    private final GuestRepository guestRepository;
    private final SharedLinkRepository sharedLinkRepository;

    @Override
    public void remove() {
        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);
        List<Guest> guests = guestRepository.findGuestsWithLastTripOneYearAgo(oneYearAgo);

        if(!guests.isEmpty()) {
            guestRepository.deleteAll(guests);
        }

        sharedLinkRepository.deleteAll(sharedLinkRepository.findSharedLinksWithUsedStatus());
    }
}