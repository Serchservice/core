package com.serch.server.domains.linked.mappers;

import com.serch.server.domains.linked.dtos.LinkedDynamicUrlDto;
import com.serch.server.domains.linked.models.LinkedDynamicUrl;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface LinkedDynamicUrlMapper {
    LinkedDynamicUrlMapper instance = Mappers.getMapper(LinkedDynamicUrlMapper.class);

    LinkedDynamicUrl url(LinkedDynamicUrlDto dto);

    LinkedDynamicUrlDto dto(LinkedDynamicUrl entity);
}