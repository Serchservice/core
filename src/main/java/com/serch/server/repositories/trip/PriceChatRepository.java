package com.serch.server.repositories.trip;

import com.serch.server.models.trip.PriceChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.UUID;

public interface PriceChatRepository extends JpaRepository<PriceChat, UUID> {
    List<PriceChat> findByHaggle_Id(@NonNull UUID id);
}