package com.serch.server.repositories.media;

import com.serch.server.models.media.MediaBlog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MediaBlogRepository extends JpaRepository<MediaBlog, String> {
}