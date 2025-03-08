package com.serch.server.domains.nearby.repositories;

import com.serch.server.domains.nearby.models.NearbyTimeline;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NearbyTimelineRepository extends JpaRepository<NearbyTimeline, Long> {
}