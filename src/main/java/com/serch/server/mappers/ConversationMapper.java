package com.serch.server.mappers;

import com.serch.server.models.conversation.ChatMessage;
import com.serch.server.domains.conversation.responses.ChatMessageResponse;
import com.serch.server.domains.conversation.responses.ChatReplyResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ConversationMapper {
    ConversationMapper INSTANCE = Mappers.getMapper(ConversationMapper.class);

    @Mappings({
            @Mapping(target = "sentAt", source = "createdAt"),
    })
    ChatMessageResponse response(ChatMessage message);

    @Mapping(target = "sender", source = "sender", ignore = true)
    ChatReplyResponse reply(ChatMessage message);
}
