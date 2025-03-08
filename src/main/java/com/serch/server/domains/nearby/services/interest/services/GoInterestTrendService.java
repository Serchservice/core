package com.serch.server.domains.nearby.services.interest.services;

import com.serch.server.domains.nearby.models.go.user.GoUserInterest;

public interface GoInterestTrendService {
    void onTrending(GoUserInterest userInterest);

    void onTrending(Long interest);
}