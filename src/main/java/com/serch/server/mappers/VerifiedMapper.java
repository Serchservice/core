package com.serch.server.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface VerifiedMapper {
    VerifiedMapper INSTANCE = Mappers.getMapper(VerifiedMapper.class);
}
