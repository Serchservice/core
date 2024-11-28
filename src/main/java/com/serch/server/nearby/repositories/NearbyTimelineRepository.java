package com.serch.server.nearby.repositories;

import com.serch.server.nearby.models.NearbyTimeline;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NearbyTimelineRepository extends JpaRepository<NearbyTimeline, Long> {
}