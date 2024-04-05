package com.serch.server.services.media.repositories;

import com.serch.server.services.media.models.MediaNewsroom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MediaNewsroomRepository extends JpaRepository<MediaNewsroom, String> {
}