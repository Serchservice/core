package com.serch.server.domains.account.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.exceptions.account.AccountException;
import com.serch.server.mappers.AccountMapper;
import com.serch.server.mappers.AuthMapper;
import com.serch.server.models.account.AdditionalInformation;
import com.serch.server.models.account.Profile;
import com.serch.server.repositories.account.AdditionalInformationRepository;
import com.serch.server.domains.account.responses.AdditionalInformationResponse;
import com.serch.server.domains.account.services.AdditionalService;
import com.serch.server.domains.auth.requests.RequestAdditionalInformation;
import com.serch.server.utils.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 * This class implements the AdditionalService interface
 * to provide functionality related to additional profile information.
 * It allows users to save incomplete additional information and view their additional profile details.
 * <p></p>
 * It implements its wrapper class {@link AdditionalService}
 *
 * @see AuthUtil
 * @see AdditionalInformationRepository
 */
@Service
@RequiredArgsConstructor
public class AdditionalImplementation implements AdditionalService {
    private final AuthUtil authUtil;
    private final AdditionalInformationRepository additionalInformationRepository;

    @Override
    public void createAdditional(RequestAdditionalInformation additional, Profile profile) {
        AdditionalInformation information = AuthMapper.INSTANCE.additional(additional);
        information.setProfile(profile);
        additionalInformationRepository.save(information);
    }

    @Override
    public ApiResponse<AdditionalInformationResponse> view() {
        AdditionalInformation information = additionalInformationRepository.findByProfile_Id(authUtil.getUser().getId())
                .orElseThrow(() -> new AccountException("User has no additional profile"));

        return new ApiResponse<>("Additional fetched successfully", AccountMapper.INSTANCE.additional(information), HttpStatus.OK);
    }
}
