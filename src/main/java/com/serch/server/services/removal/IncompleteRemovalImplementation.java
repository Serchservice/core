package com.serch.server.services.removal;

import com.serch.server.models.auth.incomplete.Incomplete;
import com.serch.server.repositories.auth.incomplete.IncompleteRepository;
import com.serch.server.utils.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IncompleteRemovalImplementation implements IncompleteRemovalService {
    private final IncompleteRepository incompleteRepository;

    @Override
    public void remove() {
        List<Incomplete> list = incompleteRepository.findByCreatedAtBefore(TimeUtil.now().minusYears(1));

        if(!list.isEmpty()) {
            incompleteRepository.deleteAll(list);
        }
    }
}