package com.serch.server.core.file.data.repositories;

import com.serch.server.core.file.data.FileUpload;
import com.serch.server.core.file.responses.CloudinaryResponse;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface FileUploadMapper {
    FileUploadMapper instance = Mappers.getMapper(FileUploadMapper.class);

    @Mappings({
            @Mapping(target = "folder", source = "assetFolder"),
            @Mapping(target = "name", source = "originalFilename")
    })
    FileUpload upload(CloudinaryResponse response);
}