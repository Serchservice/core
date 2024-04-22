package com.serch.server.services.media.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.exceptions.media.MediaBlogException;
import com.serch.server.mappers.MediaMapper;
import com.serch.server.models.media.MediaBlog;
import com.serch.server.repositories.media.MediaBlogRepository;
import com.serch.server.services.media.responses.MediaBlogResponse;
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
 * Implementation of the MediaBlogService interface for managing media blog posts.
 * It implements its wrapper class {@link MediaBlogService}
 *
 * @see MediaBlogRepository
 */
@Service
@RequiredArgsConstructor
public class MediaBlogImplementation implements MediaBlogService {
    private final MediaBlogRepository blogRepository;

    @Override
    public ApiResponse<MediaBlogResponse> findBlog(String key) {
        MediaBlog blog = blogRepository.findById(key)
                .orElseThrow(() -> new MediaBlogException("MediaBlog not found"));

        blog.setViews(blog.getViews() + 1);
        blogRepository.save(blog);
        return new ApiResponse<>("MediaBlog found", getBlogResponse(blog), HttpStatus.OK);
    }

    private MediaBlogResponse getBlogResponse(MediaBlog blog) {
        MediaBlogResponse response = MediaMapper.INSTANCE.response(blog);
        response.setRegion(MediaUtil.formatRegionAndDate(response.getCreatedAt(), blog.getRegion()));
        response.setLabel(TimeUtil.formatDay(response.getCreatedAt()));
        return response;
    }

    @Override
    public ApiResponse<List<MediaBlogResponse>> findAllBlogs(Integer page) {
        Pageable pageable = PageRequest.of(
                page != null ? page : 1,
                10,
                Sort.Order.asc("created_at").getDirection()
        );

        return new ApiResponse<>(
                "Blogs fetched",
                blogRepository.findAll(pageable)
                        .stream()
                        .sorted(Comparator.comparing(MediaBlog::getCreatedAt))
                        .map(this::getBlogResponse)
                        .toList(),
                HttpStatus.OK
        );
    }

    @Override
    public ApiResponse<List<MediaBlogResponse>> findPopularBlogs() {
        Pageable pageable = PageRequest.of(0, 5, Sort.by("views").descending());
        return new ApiResponse<>(
                "Blogs fetched",
                blogRepository.findAll(pageable)
                        .stream()
                        .sorted(Comparator.comparing(MediaBlog::getCreatedAt))
                        .map(this::getBlogResponse)
                        .toList(),
                HttpStatus.OK
        );
    }
}
