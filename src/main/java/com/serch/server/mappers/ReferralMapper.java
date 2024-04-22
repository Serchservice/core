package com.serch.server.mappers;

import com.serch.server.models.referral.ReferralProgram;
import com.serch.server.services.referral.responses.ReferralProgramData;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ReferralMapper {
    ReferralMapper INSTANCE = Mappers.getMapper(ReferralMapper.class);

    ReferralProgramData data(ReferralProgram program);
}
