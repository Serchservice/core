package com.serch.server.services.help.repositories;

import com.serch.server.services.help.models.HelpGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HelpGroupRepository extends JpaRepository<HelpGroup, String> {
}