package com.serch.server.services.auth.services.implementations;

import com.serch.server.models.auth.AccountStatusTracker;
import com.serch.server.models.auth.User;
import com.serch.server.repositories.auth.AccountStatusTrackerRepository;
import com.serch.server.services.auth.services.AccountStatusTrackerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountStatusTrackerImplementation implements AccountStatusTrackerService {
    private final AccountStatusTrackerRepository accountStatusTrackerRepository;

    @Override
    @Transactional
    public void create(User user) {
        AccountStatusTracker tracker = new AccountStatusTracker();
        tracker.setUser(user);
        tracker.setStatus(user.getStatus());
        accountStatusTrackerRepository.save(tracker);
    }
}
