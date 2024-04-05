package com.serch.server.services.media.repositories;

import com.serch.server.services.media.models.MediaAsset;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MediaAssetRepository extends JpaRepository<MediaAsset, Long> {
}