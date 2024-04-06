package com.serch.server.services.media.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.exceptions.media.MediaNewsroomException;
import com.serch.server.services.media.mappers.MediaNewsroomMapper;
import com.serch.server.models.media.MediaNewsroom;
import com.serch.server.repositories.media.MediaNewsroomRepository;
import com.serch.server.services.media.responses.MediaNewsroomResponse;
import com.serch.server.services.media.utils.MediaUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MediaNewsroomImplementation implements MediaNewsroomService {
    private final MediaNewsroomRepository newsroomRepository;

    @Override
    public ApiResponse<MediaNewsroomResponse> findNews(String key) {
        MediaNewsroom newsroom = newsroomRepository.findById(key)
                .orElseThrow(() -> new MediaNewsroomException("News not found"));

        return new ApiResponse<>("News fetched", getNewsroomResponse(newsroom), HttpStatus.OK);
    }

    private MediaNewsroomResponse getNewsroomResponse(MediaNewsroom newsroom) {
        MediaNewsroomResponse response = MediaNewsroomMapper.INSTANCE.response(newsroom);
        response.setRegion(MediaUtil.formatRegionAndDate(newsroom.getCreatedAt(), newsroom.getRegion()));
        return response;
    }

    @Override
    public ApiResponse<List<MediaNewsroomResponse>> findAllNews(Integer page) {
        Pageable pageable = PageRequest.of(
                page != null ? page : 1,
                10,
                Sort.Order.asc("created_at").getDirection()
        );

        return new ApiResponse<>(
                "News fetched",
                newsroomRepository.findAll(pageable)
                        .stream()
                        .map(this::getNewsroomResponse)
                        .toList(),
                HttpStatus.OK
        );
    }
}
