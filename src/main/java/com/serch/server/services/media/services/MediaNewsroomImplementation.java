package com.serch.server.services.media.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.exceptions.media.MediaNewsroomException;
import com.serch.server.mappers.MediaMapper;
import com.serch.server.models.media.MediaNewsroom;
import com.serch.server.repositories.media.MediaNewsroomRepository;
import com.serch.server.services.media.responses.MediaNewsroomResponse;
import com.serch.server.utils.MediaUtil;
import com.serch.server.utils.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

/**
 * Service implementation for managing media news articles.
 * It implements its wrapper class {@link MediaNewsroomService}
 *
 * @see MediaNewsroomRepository
 */
@Service
@RequiredArgsConstructor
public class MediaNewsroomImplementation implements MediaNewsroomService {
    private final MediaNewsroomRepository newsroomRepository;

    @Override
    public ApiResponse<MediaNewsroomResponse> findNews(String key) {
        MediaNewsroom newsroom = newsroomRepository.findById(key)
                .orElseThrow(() -> new MediaNewsroomException("News not found"));

        newsroom.setViews(newsroom.getViews() + 1);
        newsroomRepository.save(newsroom);
        return new ApiResponse<>("News fetched", getNewsroomResponse(newsroom), HttpStatus.OK);
    }

    private MediaNewsroomResponse getNewsroomResponse(MediaNewsroom newsroom) {
        MediaNewsroomResponse response = MediaMapper.INSTANCE.response(newsroom);
        response.setRegion(MediaUtil.formatRegionAndDate(response.getCreatedAt(), newsroom.getRegion()));
        response.setLabel(TimeUtil.formatDay(response.getCreatedAt()));
        return response;
    }

    @Override
    public ApiResponse<List<MediaNewsroomResponse>> findAllNews(Integer page) {
        Pageable pageable = PageRequest.of(
                page != null ? page : 1,
                10,
                Sort.by("createdAt").descending()
        );

        return new ApiResponse<>(
                "News fetched",
                newsroomRepository.findAll(pageable)
                        .stream()
                        .sorted(Comparator.comparing(MediaNewsroom::getCreatedAt))
                        .map(this::getNewsroomResponse)
                        .toList(),
                HttpStatus.OK
        );
    }

    @Override
    public ApiResponse<List<MediaNewsroomResponse>> findPopularNews() {
        Pageable pageable = PageRequest.of(0, 5, Sort.by("views").descending());
        return new ApiResponse<>(
                "News fetched",
                newsroomRepository.findAll(pageable)
                        .stream()
                        .map(this::getNewsroomResponse)
                        .toList(),
                HttpStatus.OK
        );
    }

    @Override
    public ApiResponse<List<MediaNewsroomResponse>> findRecentNews() {
        Pageable pageable = PageRequest.of(0, 5, Sort.by("createdAt").descending());
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
