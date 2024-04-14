package com.serch.server.services.media.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.exceptions.media.MediaBlogException;
import com.serch.server.mappers.MediaMapper;
import com.serch.server.models.media.MediaBlog;
import com.serch.server.repositories.media.MediaBlogRepository;
import com.serch.server.services.media.responses.MediaBlogResponse;
import com.serch.server.utils.MediaUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

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
        return new ApiResponse<>("MediaBlog found", getBlogResponse(blog), HttpStatus.OK);
    }

    private MediaBlogResponse getBlogResponse(MediaBlog blog) {
        MediaBlogResponse response = MediaMapper.INSTANCE.response(blog);
        response.setRegion(MediaUtil.formatRegionAndDate(blog.getCreatedAt(), blog.getRegion()));
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
                        .map(this::getBlogResponse)
                        .toList(),
                HttpStatus.OK
        );
    }
}
