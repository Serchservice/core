package com.serch.server.mappers;

import com.serch.server.models.schedule.Schedule;
import com.serch.server.services.schedule.responses.ScheduleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ScheduleMapper {
    ScheduleMapper INSTANCE = Mappers.getMapper(ScheduleMapper.class);

    @Mappings({
            @Mapping(target = "closedBy", source = "closedBy", ignore = true)
    })
    ScheduleResponse response(Schedule schedule);
}
