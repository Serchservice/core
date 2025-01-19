package com.serch.server.admin.mappers;

import com.serch.server.admin.services.auth.requests.FinishAdminSetupRequest;
import com.serch.server.domains.auth.requests.RequestLogin;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface AdminAuthMapper {
    AdminAuthMapper instance = Mappers.getMapper(AdminAuthMapper.class);

    RequestLogin login(FinishAdminSetupRequest request);
}