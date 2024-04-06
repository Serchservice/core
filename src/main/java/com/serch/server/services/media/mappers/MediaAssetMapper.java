package com.serch.server.services.media.mappers;

import com.serch.server.models.media.MediaAsset;
import com.serch.server.services.media.responses.MediaAssetResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MediaAssetMapper {
    MediaAssetMapper INSTANCE = Mappers.getMapper(MediaAssetMapper.class);

    MediaAssetResponse response(MediaAsset asset);
}
