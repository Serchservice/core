package com.serch.server.repositories.help;

import com.serch.server.models.help.HelpAsk;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HelpAskRepository extends JpaRepository<HelpAsk, UUID> {
}