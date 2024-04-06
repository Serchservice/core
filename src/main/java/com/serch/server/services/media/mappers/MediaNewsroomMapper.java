package com.serch.server.services.media.mappers;

import com.serch.server.models.media.MediaNewsroom;
import com.serch.server.services.media.responses.MediaNewsroomResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MediaNewsroomMapper {
    MediaNewsroomMapper INSTANCE = Mappers.getMapper(MediaNewsroomMapper.class);

    MediaNewsroomResponse response(MediaNewsroom newsroom);
}
