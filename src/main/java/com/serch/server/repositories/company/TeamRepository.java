package com.serch.server.repositories.company;

import com.serch.server.enums.company.TeamType;
import com.serch.server.models.company.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findByTeam(@NonNull TeamType team);
}