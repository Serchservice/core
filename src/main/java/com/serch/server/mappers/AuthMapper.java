package com.serch.server.mappers;

import com.serch.server.domains.auth.requests.*;
import com.serch.server.domains.auth.responses.MFARecoveryCodeResponse;
import com.serch.server.models.account.AdditionalInformation;
import com.serch.server.models.auth.Session;
import com.serch.server.models.auth.User;
import com.serch.server.models.auth.incomplete.Incomplete;
import com.serch.server.models.auth.incomplete.IncompletePhoneInformation;
import com.serch.server.models.auth.incomplete.IncompleteProfile;
import com.serch.server.models.auth.mfa.MFAChallenge;
import com.serch.server.models.auth.mfa.MFARecoveryCode;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface AuthMapper {
    AuthMapper INSTANCE = Mappers.getMapper(AuthMapper.class);

    @Mapping(source = "referralCode", target = "referral", ignore = true)
    @Mapping(source = "password", target = "password", ignore = true)
    RequestProfile profile(IncompleteProfile profile);

    IncompleteProfile profile(RequestProviderProfile profile);

    AdditionalInformation additional(RequestAdditionalInformation additional);

    IncompletePhoneInformation phoneInformation(RequestPhoneInformation phoneInformation);

    RequestPhoneInformation phoneInformation(IncompletePhoneInformation phoneInformation);

    Session session(RequestDevice device);

    MFAChallenge challenge(RequestDevice device);

    @Mapping(target = "code", source = "recovery")
    MFARecoveryCodeResponse response(MFARecoveryCode code);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mappings({
            @Mapping(target = "password", source = "profile.password"),
            @Mapping(target = "firstName", source = "profile.firstName"),
            @Mapping(target = "lastName", source = "profile.lastName"),
            @Mapping(target = "id", source = "id", ignore = true),
            @Mapping(target = "emailConfirmedAt", source = "tokenConfirmedAt"),
            @Mapping(target = "role", source = "role", ignore = true),
            @Mapping(target = "createdAt", source = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", source = "updatedAt", ignore = true),
    })
    User user(Incomplete incomplete);
}