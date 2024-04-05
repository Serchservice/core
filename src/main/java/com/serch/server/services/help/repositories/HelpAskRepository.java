package com.serch.server.services.help.repositories;

import com.serch.server.services.help.models.HelpAsk;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HelpAskRepository extends JpaRepository<HelpAsk, UUID> {
}