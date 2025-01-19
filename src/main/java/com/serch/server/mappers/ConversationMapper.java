package com.serch.server.mappers;

import com.serch.server.domains.conversation.requests.SendMessageRequest;
import com.serch.server.domains.conversation.responses.CallInformation;
import com.serch.server.domains.conversation.responses.CallMemberData;
import com.serch.server.domains.conversation.responses.ChatMessageResponse;
import com.serch.server.domains.conversation.responses.ChatReplyResponse;
import com.serch.server.models.account.Profile;
import com.serch.server.models.conversation.Call;
import com.serch.server.models.conversation.ChatMessage;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ConversationMapper {
    ConversationMapper INSTANCE = Mappers.getMapper(ConversationMapper.class);

    @Mapping(target = "sentAt", source = "createdAt")
    ChatMessageResponse response(ChatMessage message);

    @Mapping(target = "sender", source = "sender", ignore = true)
    ChatReplyResponse reply(ChatMessage message);

    @Mapping(target = "replied", source = "replied", ignore = true)
    ChatMessage message(SendMessageRequest request);

    CallInformation info(Call call);

    @Mappings({
            @Mapping(target = "category", source = "category.type"),
            @Mapping(target = "image", source = "category.image"),
            @Mapping(target = "name", source = "fullName"),
            @Mapping(target = "member", source = "id")
    })
    CallMemberData data(Profile profile);
}
