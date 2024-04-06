package com.serch.server.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface HelpMapper {
    HelpMapper INSTANCE = Mappers.getMapper(HelpMapper.class);
}
