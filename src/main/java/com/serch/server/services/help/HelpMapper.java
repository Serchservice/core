package com.serch.server.services.help;

import com.serch.server.services.help.models.*;
import com.serch.server.services.help.requests.HelpAskRequest;
import com.serch.server.services.help.response.*;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface HelpMapper {
    HelpMapper INSTANCE = Mappers.getMapper(HelpMapper.class);

    HelpCategoryResponse response(HelpCategory category);
    HelpSectionResponse response(HelpSection section);
    HelpGroupResponse response(HelpGroup group);
    HelpResponse response(Help help);
    HelpAsk toAsk(HelpAskRequest request);
}