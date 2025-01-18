package com.serch.server.mappers;

import com.serch.server.models.schedule.Schedule;
import com.serch.server.domains.schedule.requests.ScheduleRequest;
import com.serch.server.domains.schedule.responses.ScheduleResponse;
import com.serch.server.domains.trip.requests.TripInviteRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ScheduleMapper {
    ScheduleMapper INSTANCE = Mappers.getMapper(ScheduleMapper.class);

    @Mapping(target = "provider", source = "provider", ignore = true)
    Schedule schedule(ScheduleRequest request);

    @Mapping(target = "provider", source = "provider", ignore = true)
    TripInviteRequest request(Schedule schedule);

    @Mappings({
            @Mapping(target = "closedBy", source = "closedBy", ignore = true)
    })
    ScheduleResponse response(Schedule schedule);
}
