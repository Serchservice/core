package com.serch.server.repositories.help;

import com.serch.server.models.help.HelpGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HelpGroupRepository extends JpaRepository<HelpGroup, String> {
}