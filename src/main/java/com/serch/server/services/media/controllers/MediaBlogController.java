package com.serch.server.services.media.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.media.responses.MediaBlogResponse;
import com.serch.server.services.media.services.MediaBlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/company/media/blogs")
@RequiredArgsConstructor
public class MediaBlogController {
    private final MediaBlogService service;

    @GetMapping("/blog")
    public ResponseEntity<ApiResponse<MediaBlogResponse>> fetchBlog(@RequestParam String key) {
        ApiResponse<MediaBlogResponse> response = service.findBlog(key);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MediaBlogResponse>>> findAllBlogs(
            @RequestParam(required = false) Integer page
    ) {
        ApiResponse<List<MediaBlogResponse>> response = service.findAllBlogs(page);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<List<MediaBlogResponse>>> findPopularBlogs() {
        ApiResponse<List<MediaBlogResponse>> response = service.findPopularBlogs();
        return new ResponseEntity<>(response, response.getStatus());
    }
}
