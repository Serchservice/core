package com.serch.server.mappers;

import com.serch.server.models.auth.verified.Verification;
import com.serch.server.domains.verified.responses.VerificationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface VerifiedMapper {
    VerifiedMapper INSTANCE = Mappers.getMapper(VerifiedMapper.class);

    VerificationResponse response(Verification verification);
}
