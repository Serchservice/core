package com.serch.server.mappers;

import com.serch.server.models.account.AdditionalInformation;
import com.serch.server.models.auth.Session;
import com.serch.server.models.auth.incomplete.IncompleteAdditional;
import com.serch.server.models.auth.incomplete.IncompletePhoneInformation;
import com.serch.server.models.auth.incomplete.IncompleteProfile;
import com.serch.server.models.auth.mfa.MFAChallenge;
import com.serch.server.services.auth.requests.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AuthMapper {
    AuthMapper INSTANCE = Mappers.getMapper(AuthMapper.class);

    @Mapping(source = "referralCode", target = "referral", ignore = true)
    @Mapping(source = "password", target = "password", ignore = true)
    RequestProfile profile(IncompleteProfile profile);
    IncompleteProfile profile(RequestProviderProfile profile);
    IncompleteAdditional additional(RequestAdditionalInformation information);
    AdditionalInformation additional(IncompleteAdditional additional);
    IncompletePhoneInformation phoneInformation(RequestPhoneInformation phoneInformation);
    RequestPhoneInformation phoneInformation(IncompletePhoneInformation phoneInformation);
    Session session(RequestDevice device);
    MFAChallenge challenge(RequestDevice device);
}
