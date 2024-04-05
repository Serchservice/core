package com.serch.server.services.media.repositories;

import com.serch.server.services.media.models.MediaBlog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MediaBlogRepository extends JpaRepository<MediaBlog, String> {
}