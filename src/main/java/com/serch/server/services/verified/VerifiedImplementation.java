package com.serch.server.services.verified;

import com.serch.server.bases.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class VerifiedImplementation implements VerifiedService {
    @Override
    public ApiResponse<String> smileStatus(Map<Object, Object> data) {
        System.out.println(data);
        return new ApiResponse<>("OK");
    }
}