package com.serch.server.services.media.mappers;

import com.serch.server.models.media.MediaBlog;
import com.serch.server.services.media.responses.MediaBlogResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MediaBlogMapper {
    MediaBlogMapper INSTANCE = Mappers.getMapper(MediaBlogMapper.class);

    MediaBlogResponse response(MediaBlog blog);
}
