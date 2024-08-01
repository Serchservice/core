package com.serch.server.services.auth.services.implementations;

import com.serch.server.models.auth.AccountStatusTracker;
import com.serch.server.models.auth.User;
import com.serch.server.repositories.auth.AccountStatusTrackerRepository;
import com.serch.server.services.auth.services.AccountStatusTrackerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountStatusTrackerImplementation implements AccountStatusTrackerService {
    private final AccountStatusTrackerRepository accountStatusTrackerRepository;

    @Override
    public void create(User user) {
        AccountStatusTracker tracker = new AccountStatusTracker();
        tracker.setUser(user);
        tracker.setStatus(user.getStatus());
        accountStatusTrackerRepository.save(tracker);
    }
}
