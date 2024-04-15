package com.serch.server.services.media.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.mappers.MediaMapper;
import com.serch.server.repositories.media.MediaAssetRepository;
import com.serch.server.services.media.responses.MediaAssetResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service implementation for managing media assets. It implements its wrapper class {@link MediaAssetService}
 *
 * @see MediaAssetRepository
 */
@Service
@RequiredArgsConstructor
public class MediaAssetImplementation implements MediaAssetService {
    private final MediaAssetRepository assetRepository;

    @Override
    public ApiResponse<List<MediaAssetResponse>> fetchAllAssets() {
        return new ApiResponse<>(
                "Fetched assets",
                assetRepository.findAll()
                        .stream().map(MediaMapper.INSTANCE::response)
                        .toList(),
                HttpStatus.OK
        );
    }
}
