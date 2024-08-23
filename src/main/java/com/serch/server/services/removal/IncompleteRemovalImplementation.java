package com.serch.server.services.removal;

import com.serch.server.models.auth.incomplete.Incomplete;
import com.serch.server.repositories.auth.incomplete.IncompleteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncompleteRemovalImplementation implements IncompleteRemovalService {
    private final IncompleteRepository incompleteRepository;

    @Override
    public void remove() {
        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);
        List<Incomplete> list = incompleteRepository.findByCreatedAtBefore(oneYearAgo);

        if(!list.isEmpty()) {
            incompleteRepository.deleteAll(list);
        }
    }
}