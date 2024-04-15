package com.serch.server.mappers;

import com.serch.server.models.media.MediaAsset;
import com.serch.server.models.media.MediaBlog;
import com.serch.server.models.media.MediaLegal;
import com.serch.server.models.media.MediaNewsroom;
import com.serch.server.services.media.responses.MediaAssetResponse;
import com.serch.server.services.media.responses.MediaBlogResponse;
import com.serch.server.services.media.responses.MediaLegalResponse;
import com.serch.server.services.media.responses.MediaNewsroomResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MediaMapper {
    MediaMapper INSTANCE = Mappers.getMapper(MediaMapper.class);

    MediaAssetResponse response(MediaAsset asset);
    MediaBlogResponse response(MediaBlog blog);
    MediaLegalResponse response(MediaLegal legal);
    MediaNewsroomResponse response(MediaNewsroom newsroom);
}
