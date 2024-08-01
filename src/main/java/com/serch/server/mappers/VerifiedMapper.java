package com.serch.server.mappers;

import com.serch.server.models.auth.verified.Verification;
import com.serch.server.services.verified.responses.VerificationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface VerifiedMapper {
    VerifiedMapper INSTANCE = Mappers.getMapper(VerifiedMapper.class);

    VerificationResponse response(Verification verification);
}
