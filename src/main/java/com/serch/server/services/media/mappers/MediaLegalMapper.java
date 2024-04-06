package com.serch.server.services.media.mappers;

import com.serch.server.models.media.MediaLegal;
import com.serch.server.services.media.responses.MediaLegalResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MediaLegalMapper {
    MediaLegalMapper INSTANCE = Mappers.getMapper(MediaLegalMapper.class);

    MediaLegalResponse response(MediaLegal legal);
}
