package com.serch.server.mappers;

import com.serch.server.domains.referral.responses.ReferralProgramResponse;
import com.serch.server.domains.referral.responses.ReferralResponse;
import com.serch.server.models.referral.Referral;
import com.serch.server.models.referral.ReferralProgram;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ReferralMapper {
    ReferralMapper instance = Mappers.getMapper(ReferralMapper.class);

    @Mappings({
            @Mapping(target = "role", source = "referral.role.type"),
            @Mapping(target = "name", source = "referral.fullName"),
    })
    ReferralResponse response(Referral referral);

    @Mappings({
            @Mapping(target = "role", source = "user.role.type"),

    })
    ReferralProgramResponse response(ReferralProgram referral);
}
