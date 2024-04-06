package com.serch.server.services.account.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.models.account.Profile;
import com.serch.server.models.account.Specialty;
import com.serch.server.models.auth.incomplete.Incomplete;
import com.serch.server.repositories.account.SpecialtyRepository;
import com.serch.server.services.account.services.SpecialtyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SpecialtyImplementation implements SpecialtyService {
    private final SpecialtyRepository specialtyRepository;

    @Override
    public void saveIncompleteSpecialties(Incomplete incomplete, ApiResponse<Profile> response) {
        if(!incomplete.getSpecializations().isEmpty()) {
            incomplete.getSpecializations().forEach(specialty -> {
                Specialty special = new Specialty();
                special.setService(specialty.getService());
                special.setProfile(response.getData());
                specialtyRepository.save(special);
            });
        }
    }
}
