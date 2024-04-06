package com.serch.server.services.account.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.mappers.AuthMapper;
import com.serch.server.models.account.AdditionalInformation;
import com.serch.server.models.account.Profile;
import com.serch.server.models.auth.incomplete.Incomplete;
import com.serch.server.repositories.account.AdditionalInformationRepository;
import com.serch.server.services.account.services.AdditionalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdditionalImplementation implements AdditionalService {
    private final AdditionalInformationRepository additionalInformationRepository;

    @Override
    public void saveIncompleteAdditional(Incomplete incomplete, ApiResponse<Profile> response) {
        AdditionalInformation information = AuthMapper.INSTANCE.additional(incomplete.getAdditional());
        information.setProfile(response.getData());
        additionalInformationRepository.save(information);
    }
}
