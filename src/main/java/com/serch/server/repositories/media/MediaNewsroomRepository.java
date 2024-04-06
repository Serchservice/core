package com.serch.server.repositories.media;

import com.serch.server.models.media.MediaNewsroom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MediaNewsroomRepository extends JpaRepository<MediaNewsroom, String> {
}