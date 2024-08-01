package com.serch.server.admin.mappers;

import com.serch.server.admin.services.responses.auth.AccountMFAChallengeResponse;
import com.serch.server.admin.services.responses.auth.AccountMFAResponse;
import com.serch.server.admin.services.responses.auth.AccountSessionResponse;
import com.serch.server.models.auth.Session;
import com.serch.server.models.auth.mfa.MFAChallenge;
import com.serch.server.models.auth.mfa.MFAFactor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AnalysisMapper {
    AnalysisMapper instance = Mappers.getMapper(AnalysisMapper.class);

    @Mapping(target = "id", source = "id")
    AccountMFAResponse mfa(MFAFactor factor);

    @Mappings({
            @Mapping(target = "os", source = "operatingSystem"),
            @Mapping(target = "osv", source = "operatingSystemVersion"),
            @Mapping(target = "id", source = "id")
    })
    AccountMFAChallengeResponse challenge(MFAChallenge challenge);

    @Mappings({
            @Mapping(target = "os", source = "operatingSystem"),
            @Mapping(target = "osv", source = "operatingSystemVersion"),
            @Mapping(target = "id", source = "id")
    })
    AccountSessionResponse session(Session session);
}
