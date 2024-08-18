package com.serch.server.services.account.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.account.responses.RequestResponse;
import com.serch.server.services.account.services.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ActivityImplementation implements ActivityService {
    @Override
    @Transactional
    public ApiResponse<RequestResponse> requests() {
        return null;
    }
}