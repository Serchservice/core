package com.serch.server.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CallMapper {
    CallMapper INSTANCE = Mappers.getMapper(CallMapper.class);
}
