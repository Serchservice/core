package com.serch.server.domains.removal;

import com.serch.server.models.shared.Guest;
import com.serch.server.repositories.shared.GuestRepository;
import com.serch.server.repositories.shared.SharedLinkRepository;
import com.serch.server.utils.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GuestRemovalImplementation implements GuestRemovalService {
    private final GuestRepository guestRepository;
    private final SharedLinkRepository sharedLinkRepository;

    @Override
    @Transactional
    public void remove() {
        List<Guest> guests = guestRepository.findGuestsWithLastTripOneYearAgo(TimeUtil.now().minusYears(1));

        if(!guests.isEmpty()) {
            guestRepository.deleteAll(guests);
        }

        sharedLinkRepository.deleteAll(sharedLinkRepository.findSharedLinksWithUsedStatus());
    }
}