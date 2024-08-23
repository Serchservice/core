package com.serch.server.services.account.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.account.responses.RequestResponse;
import com.serch.server.services.account.services.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActivityImplementation implements ActivityService {
    @Override
    public ApiResponse<RequestResponse> requests() {
        return null;
    }
}