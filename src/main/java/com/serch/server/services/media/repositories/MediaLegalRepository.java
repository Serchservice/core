package com.serch.server.services.media.repositories;

import com.serch.server.services.media.models.MediaLegal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MediaLegalRepository extends JpaRepository<MediaLegal, String> {
}